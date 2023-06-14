package net.audiocall.server;

import net.audiocall.network.Message;

import java.io.IOException;

public interface MessageHandler {

    void onMessage(ClientHandler clientHandler, Message message) throws IOException;

    static MessageHandler getInstance(int type) {
        return handlers[type];
    }

    MessageHandler[] handlers = new MessageHandler[] {
            null,
            new HelloMessageHandler(),
            new ByeMessageHandler(),
            null,
            new AudioMessageHandler(),
            new CallMessageHandler(),
            new HangupMessageHandler(),
            new AcceptCallMessageHandler(),
            new DismissCallMessageHandler()
    };
}
