package net.audiocall.client.logic.network;

import net.audiocall.client.User;
import net.audiocall.network.Message;
import net.audiocall.network.MessageBye;

import java.util.Iterator;

public class ByeMessageHandler implements MessageHandler {

    @Override
    public void onMessage(NetworkService networkService, Message message) {
        Iterator<User> iterator = networkService.getUsers().iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if(user.getName().equals(((MessageBye) message).getFromUser())) {
                iterator.remove();
                break;
            }
        }
    }
}
