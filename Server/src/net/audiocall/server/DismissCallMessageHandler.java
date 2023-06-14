package net.audiocall.server;

import net.audiocall.network.Message;
import net.audiocall.network.MessageDismissCall;

import java.io.IOException;

public class DismissCallMessageHandler implements MessageHandler {

    @Override
    public void onMessage(ClientHandler clientHandler, Message message) throws IOException {
        clientHandler.getServer().dismissCall(clientHandler, (MessageDismissCall) message);
    }
}
