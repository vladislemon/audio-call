package net.audiocall.server;

import net.audiocall.network.Message;
import net.audiocall.network.MessageAcceptCall;

import java.io.IOException;

public class AcceptCallMessageHandler implements MessageHandler {

    @Override
    public void onMessage(ClientHandler clientHandler, Message message) throws IOException {
        clientHandler.getServer().acceptCall(clientHandler, (MessageAcceptCall) message);
    }
}
