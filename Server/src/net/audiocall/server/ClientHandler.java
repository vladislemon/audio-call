package net.audiocall.server;

import net.audiocall.network.Message;
import net.audiocall.network.MessageListener;
import net.audiocall.network.Transport;

import java.io.IOException;
import java.util.Objects;

public class ClientHandler {

    private static final int HELLO_TIMEOUT = 5000;

    private final Server server;
    private final Transport transport;
    private final long creationTime;
    private final MessageListener messageListener = this::onMessage;
    private String username;

    public ClientHandler(Server server, Transport transport) {
        this.server = server;
        this.transport = transport;
        this.creationTime = System.currentTimeMillis();
        transport.addMessageListener(messageListener);
    }

    private void onMessage(Transport transport, Message message) throws IOException {
        System.out.println("Incoming message: " + message);
        MessageHandler.getInstance(message.getType()).onMessage(this, message);
    }

    public boolean isHelloTimeout() {
        return System.currentTimeMillis() - creationTime > HELLO_TIMEOUT;
    }

    public int pollMessages() throws IOException {
        return transport.pollMessages();
    }

    public void sendMessage(Message message) throws IOException {
        System.out.println(this + " Outbound message: " + message);
        transport.sendMessage(message);
    }

    public void disconnect() throws IOException {
        server.onDisconnect(this);
        transport.removeMessageListener(messageListener);
        transport.close();
    }

    public Server getServer() {
        return server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientHandler that = (ClientHandler) o;

        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
