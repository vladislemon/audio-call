package net.audiocall.network;

import java.nio.ByteBuffer;

public class MessageDisconnect extends Message {

    public MessageDisconnect() {
        super(-1, Messages.MESSAGE_TYPE_DISCONNECT);
    }

    protected MessageDisconnect(long id, int type) {
        super(id, type);
    }

    @Override
    public void toBuffer(ByteBuffer buffer) {

    }

    @Override
    public void fromBuffer(ByteBuffer buffer) {

    }
}
