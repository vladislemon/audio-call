package net.audiocall.network;

import java.nio.ByteBuffer;

public class MessageAudio extends Message {

    private long callId;
    private ByteBuffer audioData;

    public MessageAudio(long callId, ByteBuffer audioData) {
        super(-1, Messages.MESSAGE_TYPE_AUDIO);
        this.callId = callId;
        this.audioData = audioData;
    }

    protected MessageAudio(long id, int type) {
        super(id, type);
    }

    public long getCallId() {
        return callId;
    }

    public ByteBuffer getAudioData() {
        return audioData;
    }

    @Override
    public void toBuffer(ByteBuffer buffer) {
        buffer.putLong(callId);
        putByteBuffer(buffer, audioData);
    }

    @Override
    public void fromBuffer(ByteBuffer buffer) {
        callId = buffer.getLong();
        audioData = getByteBuffer(buffer);
    }
}
