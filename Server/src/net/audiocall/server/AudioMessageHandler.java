package net.audiocall.server;

import net.audiocall.network.Message;
import net.audiocall.network.MessageAudio;

import java.io.IOException;

public class AudioMessageHandler implements MessageHandler {

    @Override
    public void onMessage(ClientHandler clientHandler, Message message) throws IOException {
        clientHandler.getServer().onAudioMessage(clientHandler, (MessageAudio) message);
    }
}
