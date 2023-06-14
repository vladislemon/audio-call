package net.audiocall.network;

import java.io.IOException;

public interface Transport {

    boolean isOpen();

    void open() throws IOException;

    void sendMessage(Message message) throws IOException;

    boolean addMessageListener(MessageListener listener);

    boolean removeMessageListener(MessageListener listener);

    int pollMessages() throws IOException;

    void close() throws IOException;
}
