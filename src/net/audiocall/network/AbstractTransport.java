package net.audiocall.network;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTransport implements Transport {

    private final List<MessageListener> messageListeners = new LinkedList<>();

    public boolean addMessageListener(MessageListener listener) {
        if(messageListeners.contains(listener)) {
            return false;
        }
        messageListeners.add(listener);
        return true;
    }

    @Override
    public boolean removeMessageListener(MessageListener listener) {
        return messageListeners.remove(listener);
    }

    protected void onMessage(Message message) throws IOException {
        for(MessageListener listener : messageListeners) {
            listener.onMessage(this, message);
        }
    }
}
