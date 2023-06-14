package net.audiocall.client.logic.audio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class OutputStreamAudioNode extends AbstractAudioNode {

    private final WritableByteChannel channel;

    public OutputStreamAudioNode(OutputStream outputStream) {
        this.channel = Channels.newChannel(outputStream);
    }

    @Override
    public boolean isSupported(AudioFormat format) {
        return true;
    }

    @Override
    public void onData(AudioDataChunk data) throws IOException {
        ByteBuffer buffer = data.getBuffer().duplicate();
        channel.write(buffer);
    }
}
