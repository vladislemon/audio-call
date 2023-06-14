package net.audiocall.network;

import java.nio.ByteBuffer;

public class MessageHangup extends Message {

    private long callId;

    public MessageHangup(long callId) {
        super(-1, Messages.MESSAGE_TYPE_HANGUP);
        this.callId = callId;
    }

    protected MessageHangup(long id, int type) {
        super(id, type);
    }

    public long getCallId() {
        return callId;
    }

    @Override
    public void toBuffer(ByteBuffer buffer) {
        buffer.putLong(callId);
    }

    @Override
    public void fromBuffer(ByteBuffer buffer) {
        callId = buffer.getLong();
    }
}
