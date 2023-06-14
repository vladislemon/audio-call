package net.audiocall.client.logic.network;

import net.audiocall.network.Message;
import net.audiocall.network.MessageDismissCall;

public class DismissCallMessageHandler implements MessageHandler {

    @Override
    public void onMessage(NetworkService networkService, Message message) {
        networkService.getOwner().onDismissCallMessage((MessageDismissCall) message);
    }
}
