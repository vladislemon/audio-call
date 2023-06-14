package net.audiocall.network;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class MessageCall extends Message {

    private long callId;
    private String caller;
    private String callee;
    private BigInteger p;
    private BigInteger g;
    private BigInteger A;

    public MessageCall(String caller, String callee, BigInteger p, BigInteger g, BigInteger A) {
        super(-1, Messages.MESSAGE_TYPE_CALL);
        this.caller = caller;
        this.callee = callee;
        this.p = p;
        this.g = g;
        this.A = A;
    }

    protected MessageCall(long id, int type) {
        super(id, type);
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public String getCaller() {
        return caller;
    }

    public String getCallee() {
        return callee;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getA() {
        return A;
    }

    @Override
    public void toBuffer(ByteBuffer buffer) {
        buffer.putLong(callId);
        putString(buffer, caller);
        putString(buffer, callee);
        putBigInteger(buffer, p);
        putBigInteger(buffer, g);
        putBigInteger(buffer, A);
    }

    @Override
    public void fromBuffer(ByteBuffer buffer) {
        callId = buffer.getLong();
        caller = getString(buffer);
        callee = getString(buffer);
        p = getBigInteger(buffer);
        g = getBigInteger(buffer);
        A = getBigInteger(buffer);
    }
}
