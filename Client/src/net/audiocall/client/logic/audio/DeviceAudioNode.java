package net.audiocall.client.logic.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;

public interface DeviceAudioNode extends AudioNode {

    void open(AudioFormat format) throws Exception;

    void start();

    void stop();

    void close();

    DataLine getLine();
}
