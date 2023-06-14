package net.audiocall.client.logic.network;

import net.audiocall.network.Message;

import java.io.IOException;

public interface MessageHandler {

    void onMessage(NetworkService networkService, Message message) throws IOException;

    static MessageHandler getInstance(int id) {
        return handlers[id];
    }

    MessageHandler[] handlers = new MessageHandler[] {
            null,
            new HelloMessageHandler(),
            new ByeMessageHandler(),
            new DisconnectMessageHandler(),
            new DefaultMessageAudioHandler(),
            new CallMessageHandler(),
            new HangupMessageHandler(),
            new AcceptCallMessageHandler(),
            new DismissCallMessageHandler()
    };
}
