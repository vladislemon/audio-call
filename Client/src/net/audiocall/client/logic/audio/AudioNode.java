package net.audiocall.client.logic.audio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

public interface AudioNode {

    boolean isSupported(AudioFormat format);

    void onData(AudioDataChunk data) throws IOException;

    AudioNode link(AudioNode node);

    boolean unlink(AudioNode node);
}
