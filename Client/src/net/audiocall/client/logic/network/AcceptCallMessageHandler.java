package net.audiocall.client.logic.network;

import net.audiocall.network.Message;
import net.audiocall.network.MessageAcceptCall;

public class AcceptCallMessageHandler implements MessageHandler {

    @Override
    public void onMessage(NetworkService networkService, Message message) {
        networkService.getOwner().onAcceptCallMessage((MessageAcceptCall) message);
    }
}
