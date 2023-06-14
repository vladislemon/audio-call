package net.audiocall.server;

import net.audiocall.network.Message;

import java.io.IOException;

public class ByeMessageHandler implements MessageHandler {

    @Override
    public void onMessage(ClientHandler clientHandler, Message message) throws IOException {
        clientHandler.getServer().sendToAll(message, clientHandler);
        clientHandler.disconnect();
    }
}
