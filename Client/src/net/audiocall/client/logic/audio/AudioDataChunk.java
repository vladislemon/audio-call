package net.audiocall.client.logic.audio;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

public class AudioDataChunk {

    private final AudioFormat format;
    private final ByteBuffer buffer;

    public AudioDataChunk(AudioFormat format, ByteBuffer buffer) {
        this.format = format;
        this.buffer = buffer;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public AudioDataChunk duplicate() {
        return new AudioDataChunk(format, buffer.duplicate());
    }
}
