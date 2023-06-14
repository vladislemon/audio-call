package net.audiocall.server;

import net.audiocall.network.Message;
import net.audiocall.network.MessageHangup;

import java.io.IOException;

public class HangupMessageHandler implements MessageHandler {

    @Override
    public void onMessage(ClientHandler clientHandler, Message message) throws IOException {
        clientHandler.getServer().hangup(clientHandler, (MessageHangup) message);
    }
}
