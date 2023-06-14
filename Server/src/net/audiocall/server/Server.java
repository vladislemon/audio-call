package net.audiocall.server;

import net.audiocall.network.*;

import java.io.IOException;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server {

    private final ServerSocketChannel serverSocketChannel;
    private volatile boolean isShuttingDown = false;

    private final Queue<ClientHandler> connectQueue = new LinkedList<>();
    private final Set<ClientHandler> clients = new HashSet<>();
    private final Set<ClientHandler> clientsToRemove = new HashSet<>();
    private final Map<Long, Call> calls = new HashMap<>();
    private long nextCallId;

    public Server(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        System.out.println(InetAddress.getLocalHost());
    }

    public void start() {
        Thread listeningThread = new Thread(this::listen);
        listeningThread.setDaemon(true);
        listeningThread.start();
        Thread shutdownThread = new Thread(this::shutdown);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
        loop();
    }

    public void sendToAll(Message message, ClientHandler except) throws IOException {
        for (ClientHandler client : getClients(except)) {
            client.sendMessage(message);
        }
    }

    public void sendListOfUsers(ClientHandler toClient) throws IOException {
        for (ClientHandler client : getClients(toClient)) {
            toClient.sendMessage(new MessageHello(client.getUsername()));
        }
    }

    public void call(ClientHandler caller, MessageCall message) throws IOException {
        ClientHandler callee = getClientByName(message.getCallee());
        if (callee == null || clientIsInCall(caller) || clientIsInCall(callee)
                || !caller.equals(getClientByName(message.getCaller()))) {
            return;
        }
        Call call = new Call(caller, callee);
        call.setState(Call.State.DIALING);
        message.setCallId(nextCallId++);
        callee.sendMessage(message);
        calls.put(message.getCallId(), call);
    }

    public void acceptCall(ClientHandler callee, MessageAcceptCall message) throws IOException {
        ClientHandler caller = getClientByName(message.getCaller());
        Call call = calls.get(message.getCallId());
        if(caller == null || call == null || call.getState() != Call.State.DIALING) {
            return;
        }
        call.setState(Call.State.TALKING);
        caller.sendMessage(message);
    }

    public void dismissCall(ClientHandler callee, MessageDismissCall message) throws IOException {
        ClientHandler caller = getClientByName(message.getCaller());
        Call call = calls.get(message.getCallId());
        if(caller == null || call == null || call.getState() != Call.State.DIALING) {
            return;
        }
        call.setState(Call.State.FINISHED);
        calls.remove(message.getCallId());
        caller.sendMessage(message);
    }

    public void hangup(ClientHandler client, MessageHangup message) throws IOException {
        Call call = calls.get(message.getCallId());
        if(call == null || (!client.equals(call.getCaller()) && !client.equals(call.getCallee()))) {
            return;
        }
        call.setState(Call.State.FINISHED);
        calls.remove(message.getCallId());
        call.getCallee().sendMessage(message);
        call.getCaller().sendMessage(message);
    }

    public void onAudioMessage(ClientHandler client, MessageAudio message) throws IOException {
        Call call = calls.get(message.getCallId());
        if(call == null || call.getState() != Call.State.TALKING
                || (!client.equals(call.getCaller()) && !client.equals(call.getCallee()))) {
            return;
        }
        if(client.equals(call.getCaller())) {
            call.getCallee().sendMessage(message);
        } else {
            call.getCaller().sendMessage(message);
        }
    }

    public void onDisconnect(ClientHandler client) {
        clientsToRemove.add(client);
        Map.Entry<Long, Call> callEntry = getCallEntry(client);
        if(callEntry != null) {
            Call call = callEntry.getValue();
            try {
                if(client.equals(call.getCaller())) {
                    call.getCallee().sendMessage(new MessageHangup(callEntry.getKey()));
                } else {
                    call.getCaller().sendMessage(new MessageHangup(callEntry.getKey()));
                }
            } catch (IOException e) {
                //
            }
            callEntry.getValue().setState(Call.State.FINISHED);
            calls.remove(callEntry.getKey());
        }
        try {
            sendToAll(new MessageBye(client.getUsername()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loop() {
        while (!isShuttingDown) {
            clients.removeAll(clientsToRemove);
            clientsToRemove.clear();
            serveConnectQueue();
            checkCallsDialingTimeout();
            if (pollMessages() == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

    private void serveConnectQueue() {
        synchronized (connectQueue) {
            Iterator<ClientHandler> iterator = connectQueue.iterator();
            while (iterator.hasNext()) {
                ClientHandler client = iterator.next();
                if(client.getUsername() != null) {
                    clients.add(client);
                    iterator.remove();
                    continue;
                }
                try {
                    client.pollMessages();
                    if(client.isHelloTimeout()) {
                        client.disconnect();
                        iterator.remove();
                    }
                } catch (IOException e) {
                    iterator.remove();
                }
            }
        }
    }

    private void checkCallsDialingTimeout() {
        List<Map.Entry<Long, Call>> timedOutCalls = new ArrayList<>();
        for(Map.Entry<Long, Call> entry : calls.entrySet()) {
            Call call = entry.getValue();
            if(call.getState() == Call.State.DIALING && call.isDialingTimeout()) {
                timedOutCalls.add(entry);
            }
        }
        for(Map.Entry<Long, Call> entry : timedOutCalls) {
            ClientHandler caller = entry.getValue().getCaller();
            MessageHangup message = new MessageHangup(entry.getKey());
            try {
                hangup(caller, message);
            } catch (IOException e) {
                //
            }
        }
    }

    private List<ClientHandler> getClients(ClientHandler except) {
        List<ClientHandler> list = new ArrayList<>();
        for (ClientHandler client : clients) {
            if (!clientsToRemove.contains(client) && !client.equals(except)) {
                list.add(client);
            }
        }
        return list;
    }

    private ClientHandler getClientByName(String name) {
        for (ClientHandler client : getClients(null)) {
            if (client.getUsername().equals(name)) {
                return client;
            }
        }
        return null;
    }

    private boolean clientIsInCall(ClientHandler client) {
        for (Map.Entry<Long, Call> entry : calls.entrySet()) {
            Call call = entry.getValue();
            if (call.getCaller().equals(client) || call.getCallee().equals(client)) {
                return true;
            }
        }
        return false;
    }

    private Map.Entry<Long, Call> getCallEntry(ClientHandler client) {
        for (Map.Entry<Long, Call> entry : calls.entrySet()) {
            Call call = entry.getValue();
            if (call.getCaller().equals(client) || call.getCallee().equals(client)) {
                return entry;
            }
        }
        return null;
    }

    private int pollMessages() {
        int read = 0;
        for (ClientHandler clientHandler : clients) {
            try {
                read += clientHandler.pollMessages();
            } catch (IOException e) {
                //e.printStackTrace();
                try {
                    clientHandler.disconnect();
                } catch (IOException e1) {
                    //
                }
            }
        }
        return read;
    }

    private void listen() {
        while (serverSocketChannel.isOpen()) {
            try {
                SocketChannel channel = serverSocketChannel.accept();
                Transport transport = new SocketTransport(channel, false);
                transport.open();
                ClientHandler clientHandler = new ClientHandler(this, transport);
                synchronized (connectQueue) {
                    connectQueue.offer(clientHandler);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown() {
        isShuttingDown = true;
        try {
            sendToAll(new MessageDisconnect(), null);
        } catch (IOException e) {
            //
        }
    }
}
