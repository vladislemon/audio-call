package net.audiocall.network;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class MessageAcceptCall extends Message {

    private long callId;
    private String caller;
    private String callee;
    private BigInteger B;

    public MessageAcceptCall(long callId, String caller, String callee, BigInteger B) {
        super(-1, Messages.MESSAGE_TYPE_ACCEPT_CALL);
        this.callId = callId;
        this.caller = caller;
        this.callee = callee;
        this.B = B;
    }

    protected MessageAcceptCall(long id, int type) {
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

    public BigInteger getB() {
        return B;
    }

    @Override
    public void toBuffer(ByteBuffer buffer) {
        buffer.putLong(callId);
        putString(buffer, caller);
        putString(buffer, callee);
        putBigInteger(buffer, B);
    }

    @Override
    public void fromBuffer(ByteBuffer buffer) {
        callId = buffer.getLong();
        caller = getString(buffer);
        callee = getString(buffer);
        B = getBigInteger(buffer);
    }
}
