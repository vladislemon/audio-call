package net.audiocall.network;

import java.nio.ByteBuffer;

public class MessageHello extends Message {

    private String fromUser;

    public MessageHello(String fromUser) {
        super(-1, Messages.MESSAGE_TYPE_HELLO);
        this.fromUser = fromUser;
    }

    protected MessageHello(long id, int type) {
        super(id, type);
    }

    public String getFromUser() {
        return fromUser;
    }

    @Override
    public void toBuffer(ByteBuffer buffer) {
        putString(buffer, fromUser);
    }

    @Override
    public void fromBuffer(ByteBuffer buffer) {
        fromUser = getString(buffer);
    }
}
