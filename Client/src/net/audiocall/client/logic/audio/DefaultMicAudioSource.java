package net.audiocall.client.logic.audio;

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DefaultMicAudioSource extends AbstractDeviceAudioNode implements DeviceAudioSource {

    public DefaultMicAudioSource(int lineBufferSize) {
        super(DeviceType.TARGET_DATA_LINE, lineBufferSize);
    }

    public DefaultMicAudioSource() {
        super(DeviceType.TARGET_DATA_LINE);
    }

    @Override
    public int pollData(ByteBuffer buffer) throws IOException {
        int length = readDataInternal(buffer);
        sendDataToLinkedNodes(new AudioDataChunk(dataLine.getFormat(), buffer));
        return length;
    }

    private int readDataInternal(ByteBuffer buffer) {
        buffer.clear();
        int length = Math.min(buffer.remaining(), lineBufferSize);
        length = ((TargetDataLine) dataLine).read(internalBuffer, 0, length);
        buffer.put(internalBuffer, 0, length);
        buffer.flip();
        return length;
    }
}
