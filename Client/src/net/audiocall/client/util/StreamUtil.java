package net.audiocall.client.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StreamUtil {

    public static String printStackTraceToString(Exception e) {
        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream(4096);
        e.printStackTrace(new PrintStream(bufferStream));
        return bufferStream.toString();
    }
}
