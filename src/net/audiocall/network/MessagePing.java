package net.audiocall.network;

import java.nio.ByteBuffer;

public class MessagePing extends Message {

    public MessagePing() {
        super(-1, Messages.MESSAGE_TYPE_PING);
    }

    protected MessagePing(long id, int type) {
        super(id, type);
    }

    @Override
    public void toBuffer(ByteBuffer buffer) {

    }

    @Override
    public void fromBuffer(ByteBuffer buffer) {

    }
}
