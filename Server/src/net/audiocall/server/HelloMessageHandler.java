package net.audiocall.server;

import net.audiocall.network.Message;
import net.audiocall.network.MessageHello;

import java.io.IOException;

public class HelloMessageHandler implements MessageHandler {

    @Override
    public void onMessage(ClientHandler clientHandler, Message message) throws IOException {
        MessageHello messageHello = (MessageHello) message;
        clientHandler.setUsername(messageHello.getFromUser());
        clientHandler.getServer().sendToAll(message, clientHandler);
        clientHandler.getServer().sendListOfUsers(clientHandler);
    }
}
