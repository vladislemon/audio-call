package net.audiocall.network;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class Message {

    private final long id;
    private final int type;

    public Message(long id, int type) {
        this.id = id;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    protected void putByteArray(ByteBuffer buffer, byte[] array) {
        buffer.putInt(array.length);
        buffer.put(array);
    }

    protected byte[] getByteArray(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] array = new byte[length];
        buffer.get(array);
        return array;
    }

    protected void putByteBuffer(ByteBuffer buffer, ByteBuffer data) {
        buffer.putInt(data.remaining());
        buffer.put(data);
    }

    protected ByteBuffer getByteBuffer(ByteBuffer buffer) {
        int length = buffer.getInt();
        return buffer.slice().limit(length);
    }

    protected void putString(ByteBuffer buffer, String s) {
        putByteArray(buffer, s.getBytes(StandardCharsets.UTF_8));
    }

    protected String getString(ByteBuffer buffer) {
        return new String(getByteArray(buffer), StandardCharsets.UTF_8);
    }

    protected void putBigInteger(ByteBuffer buffer, BigInteger n) {
        putByteArray(buffer, n.toByteArray());
    }

    protected BigInteger getBigInteger(ByteBuffer buffer) {
        return new BigInteger(getByteArray(buffer));
    }

    public abstract void toBuffer(ByteBuffer buffer);

    public abstract void fromBuffer(ByteBuffer buffer);
}
