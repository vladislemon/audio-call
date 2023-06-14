package net.audiocall.client.logic.audio;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface DeviceAudioSource extends DeviceAudioNode {

    int pollData(ByteBuffer buffer) throws IOException;
}
