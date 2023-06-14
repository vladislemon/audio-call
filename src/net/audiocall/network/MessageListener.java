package net.audiocall.network;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface MessageListener {

    void onMessage(Transport source, Message message) throws IOException;
}
