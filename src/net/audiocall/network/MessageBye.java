package net.audiocall.network;

import java.nio.ByteBuffer;

public class MessageBye extends Message {

    private String fromUser;

    public MessageBye(String fromUser) {
        super(-1, Messages.MESSAGE_TYPE_BYE);
        this.fromUser = fromUser;
    }

    protected MessageBye(long id, int type) {
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
