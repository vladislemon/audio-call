package net.audiocall.client;

import net.audiocall.Constants;
import net.audiocall.client.crypt.DiffieHellmanAuthState;
import net.audiocall.client.crypt.DiffieHellmanGenerator;
import net.audiocall.client.crypt.DiffieHellmanState;
import net.audiocall.client.logic.audio.*;
import net.audiocall.client.logic.network.DefaultMessageAudioHandler;
import net.audiocall.client.logic.network.MessageHandler;
import net.audiocall.client.logic.network.NetworkService;
import net.audiocall.client.ui.AcceptCallDialog;
import net.audiocall.client.ui.ApplicationScreen;
import net.audiocall.client.util.FileUtil;
import net.audiocall.network.*;
import net.audiocall.client.util.ObservableList;
import net.audiocall.client.util.StreamUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Application extends JFrame {

    private final ResourceBundle resourceBundle;
    private final ApplicationScreen screen;
    private final ObservableList<User> users = new ObservableList<>(new ArrayList<>());
    private final SecureRandom random = new SecureRandom();
    private NetworkService networkService;
    private DeviceAudioSource mic;
    private DeviceAudioNode speaker;
    private AudioNode encodingNode;
    private AudioNode decodingNode;
    private AudioNode sendingNode;
    private AudioNode receivingNode;
    private String inCallWith;
    private long callId = -1;
    private DiffieHellmanState dhState;
    private byte[] sharedSecret;
    private AcceptCallDialog acceptCallDialog;

    public Application() throws Exception {
        super(Constants.CLIENT_TITLE);
        loadIcon();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        this.resourceBundle = ResourceBundle.getBundle(Constants.CLIENT_RESOURCE_BUNDLE_NAME);

        this.setResizable(Constants.CLIENT_WINDOW_IS_RESIZEABLE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Application.this.close();
            }
        });

        this.screen = new ApplicationScreen(this, resourceBundle, users);
        this.add(screen.getRoot());
        this.pack();
        this.screen.packTabs();
        this.setLocationRelativeTo(null);
    }

    private void loadIcon() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Constants.CLIENT_ICON_FILE_NAME);
        if(inputStream == null) {
            throw new IOException();
        }
        setIconImage(ImageIO.read(inputStream));
        inputStream.close();
    }

    private void start() throws Exception {
        networkService = new NetworkService(this, users);
        speaker = new DefaultSpeakerAudioNode(Constants.CLIENT_AUDIO_LINE_BUFFER_SIZE);
        mic = new DefaultMicAudioSource(Constants.CLIENT_AUDIO_BUFFER_SIZE);
        speaker.open(DefaultMessageAudioHandler.getDefaultFormat());
        mic.open(DefaultMessageAudioHandler.getDefaultFormat());
        speaker.start();
        this.setVisible(true);
    }

    private void startAudioSendingThread() {
        Thread audioSendingThread = new Thread(
                () -> {
                    ByteBuffer buffer = ByteBuffer.allocate(Constants.CLIENT_AUDIO_BUFFER_SIZE);
                    mic.start();
                    while (networkService.isConnected() && inCallWith != null && callId != -1) {
                        try {
                            mic.pollData(buffer);
                        } catch (IOException e) {
                            //
                        }
                    }
                    mic.stop();
                }
        );
        audioSendingThread.setDaemon(true);
        audioSendingThread.start();
    }

    private void close() {
        try {
            networkService.disconnect(true);
        } catch (IOException e) {
            //
        }
        speaker.stop();
        speaker.close();
        mic.stop();
        mic.close();
        screen.dispose();
        this.dispose();
        System.exit(0);
    }

    public void connectToServer(String host, int port, String username) {
        try {
            networkService.connect(host, port, username);
            screen.onConnect(username);
        } catch (IOException e) {
            try {
                e.printStackTrace();
                networkService.disconnect(false);
            } catch (IOException e1) {
                //
            }
        }
    }

    public boolean isConnected() {
        return networkService.isConnected();
    }

    public void disconnect() {
        closeAcceptCallDialog();
        try {
            networkService.disconnect(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        screen.onDisconnect();
    }

    public void callOrHangup(String username) {
        if (inCallWith == null && username != null) {
            BigInteger[] numbers = generateDiffieHellmanNumbers();
            try {
                createDiffieHellmanState(numbers[0], numbers[1], username);
            } catch (IllegalStateException e) {
                onUserHasNoTrust(username);
                return;
            }
            String caller = networkService.getUsername();
            MessageCall message = new MessageCall(caller, username, numbers[0], numbers[1], dhState.getPublicKey());
            try {
                networkService.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (inCallWith != null && callId != -1) {
            try {
                networkService.sendMessage(new MessageHangup(callId));
                onHangup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCallMessage(MessageCall message) {
        if (!message.getCallee().equals(networkService.getUsername())) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                createDiffieHellmanState(message.getP(), message.getG(), message.getCaller());
            } catch (IllegalStateException e) {
                return;
            }
            Application.this.acceptCallDialog = new AcceptCallDialog(Application.this, resourceBundle, message.getCaller(), isAccepted -> {
                if (isAccepted) {
                    try {
                        networkService.sendMessage(new MessageAcceptCall(message.getCallId(), message.getCaller(),
                                message.getCallee(), dhState.getPublicKey()));
                    } catch (IOException e) {
                        return;
                    }
                    generateSharedSecret(message.getA());
                    onCall(message.getCallId(), message.getCaller());
                } else {
                    try {
                        networkService.sendMessage(new MessageDismissCall(message.getCallId(),
                                message.getCaller(), message.getCallee()));
                    } catch (IOException e) {
                        //
                    }
                }
            });
            Application.this.acceptCallDialog.setVisible(true);
        });
    }

    public void onAcceptCallMessage(MessageAcceptCall message) {
        if (!message.getCaller().equals(networkService.getUsername())) {
            return;
        }
        generateSharedSecret(message.getB());
        onCall(message.getCallId(), message.getCallee());
    }

    public void onDismissCallMessage(MessageDismissCall message) {
        if (!message.getCaller().equals(networkService.getUsername())) {
            return;
        }
        onHangup();
    }

    private BigInteger[] generateDiffieHellmanNumbers() {
        return DiffieHellmanGenerator.generateNumbersPG(Constants.CLIENT_DH_P_BIT_LENGTH, random);
    }

    private void createDiffieHellmanState(BigInteger p, BigInteger g, String withUser) throws IllegalStateException {
        //dhState = new DiffieHellmanState(p, g, random, Constants.CLIENT_DH_KEY_BIT_LENGTH);
        String kPath = Constants.CLIENT_DH_NUMBERS_PATH + File.separator + withUser + File.separator + "k.txt";
        String mPath = Constants.CLIENT_DH_NUMBERS_PATH + File.separator + withUser + File.separator + "m.txt";
        BigInteger k = FileUtil.readBigInteger(kPath);
        BigInteger m = FileUtil.readBigInteger(mPath);
        if(k == null || m == null) {
            throw new IllegalStateException(String.format("Not found DH numbers for user '%s'", withUser));
        }
        dhState = new DiffieHellmanAuthState(p, g, k, m, random, Constants.CLIENT_DH_KEY_BIT_LENGTH);
    }

    private void generateSharedSecret(BigInteger anotherPublicKey) {
        sharedSecret = DiffieHellmanGenerator.getNumberBytes(dhState.getSharedSecret(anotherPublicKey));
    }

    private void initNodes() {
        encodingNode = new NASHCipherAudioNode(sharedSecret, true);
        decodingNode = new NASHCipherAudioNode(sharedSecret, false);
        sendingNode = new MessageSendingAudioNode(DefaultMessageAudioHandler.getDefaultFormat(), networkService, callId);
        receivingNode = (AudioNode) MessageHandler.getInstance(Messages.MESSAGE_TYPE_AUDIO);

        mic.link(encodingNode).link(sendingNode);
        receivingNode.link(decodingNode).link(speaker);
    }

    private void destroyNodes() {
        mic.unlink(encodingNode);
        receivingNode.unlink(decodingNode);

        encodingNode = null;
        decodingNode = null;
        sendingNode = null;
        receivingNode = null;
    }

    private void closeAcceptCallDialog() {
        if(acceptCallDialog != null) {
            acceptCallDialog.dispose();
            acceptCallDialog = null;
        }
    }

    private void onUserHasNoTrust(String username) {
        String message = String.format(resourceBundle.getString("UntrustedUser.text"), username);
        JOptionPane.showMessageDialog(this, message, Constants.CLIENT_UNTRUSTED_TITLE, JOptionPane.WARNING_MESSAGE);
    }

    public void onCall(long callId, String with) {
        this.callId = callId;
        this.inCallWith = with;
        initNodes();
        startAudioSendingThread();
        screen.onCall(with);
    }

    public void onHangup() {
        if (callId != -1) {
            destroyNodes();
        }
        closeAcceptCallDialog();
        inCallWith = null;
        callId = -1;
        screen.onHangup();
    }

    public static void main(String[] args) {
        try {
            new Application().start();
        } catch (Exception e) {
            e.printStackTrace();
            String message = StreamUtil.printStackTraceToString(e);
            JOptionPane.showMessageDialog(null, message, Constants.CLIENT_EXCEPTION_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
}
