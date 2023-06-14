package net.audiocall.util;

import java.nio.ByteBuffer;

public class BufferUtil {

    public static void copyInsideByteBuffer(ByteBuffer buffer, int src, int dst, int length) {
        if(buffer.hasArray()) {
            byte[] array = buffer.array();
            int offset = buffer.arrayOffset();
            System.arraycopy(array, offset + src, array, offset + dst, length);
        } else {
            ByteBuffer srcBuffer = buffer.duplicate();
            ByteBuffer dstBuffer = buffer.duplicate();
            srcBuffer.limit(src + length);
            srcBuffer.position(src);
            dstBuffer.limit(dst + length);
            dstBuffer.position(dst);
            dstBuffer.put(srcBuffer);
        }
    }
}
