package net.audiocall.client.logic.network;

import net.audiocall.client.User;
import net.audiocall.client.Application;
import net.audiocall.network.*;
import net.audiocall.client.util.ObservableList;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkService {

    private final Application owner;
    private final MessageListener messageListener = this::onMessage;
    private final ObservableList<User> users;
    private final Object transportLock = new Object();
    private Transport transport;
    private String username;

    public NetworkService(Application owner, ObservableList<User> users) {
        this.owner = owner;
        this.users = users;
    }

    public Application getOwner() {
        return owner;
    }

    public String getUsername() {
        return username;
    }

    public boolean isConnected() {
        return transport != null && transport.isOpen();
    }

    public void connect(String host, int port, String username) throws IOException {
        this.username = username;
        synchronized (transportLock) {
            transport = new SocketTransport(InetAddress.getByName(host), port, false);
            transport.addMessageListener(messageListener);
            transport.open();
        }
        sendMessage(new MessageHello(username));
        startPollingThread();
    }

    public void disconnect(boolean sendBye) throws IOException {
        synchronized (transportLock) {
            if (transport != null && transport.isOpen()) {
                users.clear();
                if(sendBye) {
                    try {
                        sendMessage(new MessageBye(username));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                transport.removeMessageListener(messageListener);
                transport.close();
            }
        }
    }

    public int pollMessages() throws IOException {
        synchronized (transportLock) {
            return transport.pollMessages();
        }
    }

    public void sendMessage(Message message) throws IOException {
        System.out.println("Outbound message: " + message);
        synchronized (transportLock) {
            transport.sendMessage(message);
        }
    }

    private void onMessage(Transport transport, Message message) throws IOException {
        System.out.println("Incoming message: " + message);
        MessageHandler.getInstance(message.getType()).onMessage(this, message);
    }

    private void startPollingThread() {
        AtomicBoolean isRunning = new AtomicBoolean(true);
        Thread pollingThread = new Thread(() -> {
            while (isRunning.get()) {
                try {
                    if(pollMessages() == 0) {
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
                    try {
                        e.printStackTrace();
                        disconnect(true);
                    } catch (IOException e1) {
                        //
                    } finally {
                        isRunning.set(false);
                    }
                }
            }
        });
        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    public ObservableList<User> getUsers() {
        return users;
    }
}
