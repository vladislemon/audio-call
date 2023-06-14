package net.audiocall.client.logic.network;

import net.audiocall.client.User;
import net.audiocall.network.Message;
import net.audiocall.network.MessageHello;

public class HelloMessageHandler implements MessageHandler {

    @Override
    public void onMessage(NetworkService networkService, Message message) {
        networkService.getUsers().add(new User(((MessageHello) message).getFromUser()));
    }
}
