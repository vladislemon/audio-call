package net.audiocall.client.logic.audio;

import net.audiocall.Constants;
import net.audiocall.client.logic.network.NetworkService;
import net.audiocall.network.MessageAudio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageSendingAudioNode implements AudioNode {

    private final AudioFormat format;
    private final NetworkService networkService;
    private final long callId;
    private final ByteBuffer buffer;

    public MessageSendingAudioNode(AudioFormat format, NetworkService networkService, long callId) {
        this.format = format;
        this.networkService = networkService;
        this.callId = callId;
        this.buffer = ByteBuffer.allocate(Constants.CLIENT_AUDIO_BUFFER_SIZE);
    }

    @Override
    public boolean isSupported(AudioFormat format) {
        return this.format.equals(format);
    }

    @Override
    public void onData(AudioDataChunk data) throws IOException {
        buffer.clear();
        buffer.put(data.getBuffer().duplicate());
        buffer.flip();
        networkService.sendMessage(new MessageAudio(callId, buffer));
    }

    @Override
    public AudioNode link(AudioNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean unlink(AudioNode node) {
        throw new UnsupportedOperationException();
    }
}
