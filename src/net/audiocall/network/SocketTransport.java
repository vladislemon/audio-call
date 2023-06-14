package net.audiocall.network;

import net.audiocall.util.BufferUtil;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketTransport extends AbstractTransport {

    //private static final int TIME_OUT = 5000; //ms
    private static final int HEADER_LENGTH = 16;
    private static final int READ_BUFFER_SIZE = 1024*1024;
    private static final int WRITE_BUFFER_SIZE = 1024*128;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    private final ByteBuffer writeBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);
    private final InetSocketAddress peerAddress;
    private final SocketChannel channel;
    private final boolean isBlocking;

    private long messageCounter = 0;
    //private final BufferedWriter log = Files.newBufferedWriter(Paths.get(String.format("packet_log_%d.txt", System.currentTimeMillis())), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    public SocketTransport(SocketChannel channel, boolean isBlocking) {
        this.channel = channel;
        this.peerAddress = null;
        this.isBlocking = isBlocking;
    }

    public SocketTransport(InetAddress address, int port, boolean isBlocking) throws IOException {
        this.channel = SocketChannel.open();
        this.peerAddress = new InetSocketAddress(address, port);
        this.isBlocking = isBlocking;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void open() throws IOException {
        if(!channel.isConnected()) {
            channel.connect(peerAddress);
        }
        channel.configureBlocking(isBlocking);
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        writeBuffer.clear();
        if(message.getId() > -1) {
            writeBuffer.putLong(message.getId());
        } else {
            writeBuffer.putLong(messageCounter++);
        }
        writeBuffer.putInt(message.getType());
        writeBuffer.putInt(0);
        int position = writeBuffer.position();
        message.toBuffer(writeBuffer);
        int messageLength = writeBuffer.position() - position;
        writeBuffer.position(position - 4);
        writeBuffer.putInt(messageLength);
        writeBuffer.position(position + messageLength);
        writeBuffer.flip();

//        String l = id == Messages.MESSAGE_ID_AUDIO ?
//                String.format("Outbound Packet: number=%d, id=%d, length=%d, audioID=%d\n", messageCount, (int) id, (int) length, bodyBuffer.duplicate().getInt()) :
//                String.format("Outbound Packet: number=%d, id=%d, length=%d\n", messageCount, (int) id, (int) length);
//        log.write(l);
//        log.flush();

        while (writeBuffer.remaining() > 0) {
            channel.write(writeBuffer);
        }
    }

    @Override
    public int pollMessages() throws IOException {
        if(!isOpen()) return 0;
        int readPosition = 0;
        int readLength;
        while(channel.read(readBuffer) > 0) {
            do {
                ByteBuffer frameBuffer = readBuffer.duplicate().flip().position(readPosition);
                readLength = tryReadMessage(frameBuffer.slice());
                readPosition += readLength;
            } while (readLength > 0);
        }
        if(readPosition > 0) {
            int length = readBuffer.position() - readPosition;
            BufferUtil.copyInsideByteBuffer(readBuffer, readPosition, 0, length);
            readBuffer.position(length);
        }
        return readPosition;
    }

    private int tryReadMessage(ByteBuffer messageBuffer) throws IOException {
        if(messageBuffer.remaining() >= HEADER_LENGTH) {
            long id = messageBuffer.getLong();
            int type = messageBuffer.getInt();
            int length = messageBuffer.getInt();
            if(messageBuffer.remaining() >= length) {
                messageBuffer.limit(HEADER_LENGTH + length);

//                String l = id == Messages.MESSAGE_ID_AUDIO ?
//                        String.format("Inbound Packet: number=%d, id=%d, length=%d, audioID=%d\n", number, (int) id, (int) length, messageBuffer.duplicate().getInt()) :
//                        String.format("Inbound Packet: number=%d, id=%d, length=%d\n", number, (int) id, (int) length);
//                log.write(l);
//                log.flush();
                Message message = Messages.createMessage(id, type);
                if(message != null) {
                    message.fromBuffer(messageBuffer.slice());
                    onMessage(message);
                }
                return HEADER_LENGTH + length;
            }
        }
        return 0;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
