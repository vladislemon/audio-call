package net.audiocall.network;

import java.nio.ByteBuffer;

public class MessageDismissCall extends Message {

    private long callId;
    private String caller;
    private String callee;

    public MessageDismissCall(long callId, String caller, String callee) {
        super(-1, Messages.MESSAGE_TYPE_DISMISS_CALL);
        this.callId = callId;
        this.caller = caller;
        this.callee = callee;
    }

    public MessageDismissCall(long id, int type) {
        super(id, type);
    }

    public long getCallId() {
        return callId;
    }

    public String getCaller() {
        return caller;
    }

    public String getCallee() {
        return callee;
    }

    @Override
    public void toBuffer(ByteBuffer buffer) {
        buffer.putLong(callId);
        putString(buffer, caller);
        putString(buffer, callee);
    }

    @Override
    public void fromBuffer(ByteBuffer buffer) {
        callId = buffer.getLong();
        caller = getString(buffer);
        callee = getString(buffer);
    }
}
