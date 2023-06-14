package net.audiocall.client.logic.network;

import net.audiocall.client.logic.audio.AbstractAudioNode;
import net.audiocall.client.logic.audio.AudioDataChunk;
import net.audiocall.network.Message;
import net.audiocall.network.MessageAudio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageAudioHandler extends AbstractAudioNode implements MessageHandler {

    private final AudioFormat format;
    private final ByteBuffer buffer;
    private final ExecutorService writingExecutor;

    public MessageAudioHandler(AudioFormat format, int bufferSize) {
        this.format = format;
        this.buffer = ByteBuffer.allocate(bufferSize);
        this.writingExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public boolean isSupported(AudioFormat format) {
        return this.format.equals(format);
    }

    @Override
    public void onMessage(NetworkService networkService, Message message) {
        //System.out.println(((MessageAudio) message).getAudioData().remaining());
        synchronized (buffer) {
            buffer.clear();
            buffer.put(((MessageAudio) message).getAudioData());
            buffer.flip();
        }
        writingExecutor.submit(() -> {
            try {
                sendDataToLinkedNodes(new AudioDataChunk(format, buffer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
