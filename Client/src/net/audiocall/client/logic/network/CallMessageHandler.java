package net.audiocall.client.logic.network;

import net.audiocall.network.Message;
import net.audiocall.network.MessageCall;

import java.io.IOException;

public class CallMessageHandler implements MessageHandler {

    @Override
    public void onMessage(NetworkService networkService, Message message) throws IOException {
        networkService.getOwner().onCallMessage((MessageCall) message);
    }
}
