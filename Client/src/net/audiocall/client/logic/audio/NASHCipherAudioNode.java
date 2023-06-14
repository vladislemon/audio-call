package net.audiocall.client.logic.audio;

import net.audiocall.client.crypt.NASHCipher;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NASHCipherAudioNode extends AbstractAudioNode {

    private final NASHCipher cipher;
    private final boolean isEncodingNode;

    public NASHCipherAudioNode(byte[] sharedSecret, boolean isEncodingNode) {
        this.cipher = new NASHCipher(sharedSecret);
        this.isEncodingNode = isEncodingNode;
    }

    @Override
    public boolean isSupported(AudioFormat format) {
        return true;
    }

    @Override
    public void onData(AudioDataChunk data) throws IOException {
        byte[] bytes = new byte[data.getBuffer().remaining()];
        data.getBuffer().duplicate().get(bytes);
        if(isEncodingNode) {
            cipher.encodeBytes(bytes, 0, bytes.length);
        } else {
            cipher.decodeBytes(bytes, 0, bytes.length);
        }
        sendDataToLinkedNodes(new AudioDataChunk(data.getFormat(), ByteBuffer.wrap(bytes)));
    }
}
