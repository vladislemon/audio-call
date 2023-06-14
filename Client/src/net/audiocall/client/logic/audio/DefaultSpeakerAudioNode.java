package net.audiocall.client.logic.audio;

import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;

public class DefaultSpeakerAudioNode extends AbstractDeviceAudioNode {

    public DefaultSpeakerAudioNode(int lineBufferSize) {
        super(DeviceType.SOURCE_DATA_LINE, lineBufferSize);
    }

    public DefaultSpeakerAudioNode() {
        super(DeviceType.SOURCE_DATA_LINE);
    }

    @Override
    public void onData(AudioDataChunk data) {
        ByteBuffer buffer = data.getBuffer();
        //System.out.println("On Audio Data: " + buffer.remaining());
        synchronized (buffer) {
            if (buffer.hasArray()) {
                ((SourceDataLine) dataLine).write(buffer.array(), buffer.arrayOffset(), buffer.remaining());
            } else {
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                ((SourceDataLine) dataLine).write(bytes, 0, bytes.length);
            }
        }
    }
}
