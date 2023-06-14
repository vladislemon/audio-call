package net.audiocall.client.logic.network;

import net.audiocall.network.Message;

public class HangupMessageHandler implements MessageHandler {

    @Override
    public void onMessage(NetworkService networkService, Message message) {
        networkService.getOwner().onHangup();
    }
}
