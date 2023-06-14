package net.audiocall.server;

import net.audiocall.network.Message;
import net.audiocall.network.MessageCall;

import java.io.IOException;

public class CallMessageHandler implements MessageHandler {

    @Override
    public void onMessage(ClientHandler clientHandler, Message message) throws IOException {
        clientHandler.getServer().call(clientHandler, (MessageCall) message);
    }
}
