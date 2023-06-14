package net.audiocall.client.logic.network;

import net.audiocall.Constants;

import javax.sound.sampled.AudioFormat;

public class DefaultMessageAudioHandler extends MessageAudioHandler {

    private static final AudioFormat defaultFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            Constants.CLIENT_AUDIO_FORMAT_SAMPLE_RATE,
            Constants.CLIENT_AUDIO_FORMAT_SAMPLE_SIZE,
            Constants.CLIENT_AUDIO_FORMAT_CHANNELS,
            Constants.CLIENT_AUDIO_FORMAT_FRAME_SIZE,
            Constants.CLIENT_AUDIO_FORMAT_FRAME_RATE,
            Constants.CLIENT_AUDIO_FORMAT_BIG_ENDIAN
    );

    public static AudioFormat getDefaultFormat() {
        return defaultFormat;
    }

    public DefaultMessageAudioHandler() {
        super(defaultFormat, Constants.CLIENT_AUDIO_BUFFER_SIZE);
    }
}
