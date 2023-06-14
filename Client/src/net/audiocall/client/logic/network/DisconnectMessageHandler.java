package net.audiocall.client.logic.network;

import net.audiocall.network.Message;

import java.io.IOException;

public class DisconnectMessageHandler implements MessageHandler {

    @Override
    public void onMessage(NetworkService networkService, Message message) throws IOException {
        networkService.disconnect(false);
    }
}
