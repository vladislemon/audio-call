package net.audiocall.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Messages {

    public static final int MESSAGE_TYPE_PING = 0;
    public static final int MESSAGE_TYPE_HELLO = 1;
    public static final int MESSAGE_TYPE_BYE = 2;
    public static final int MESSAGE_TYPE_DISCONNECT = 3;
    public static final int MESSAGE_TYPE_AUDIO = 4;
    public static final int MESSAGE_TYPE_CALL = 5;
    public static final int MESSAGE_TYPE_HANGUP = 6;
    public static final int MESSAGE_TYPE_ACCEPT_CALL = 7;
    public static final int MESSAGE_TYPE_DISMISS_CALL = 8;

    private static final Class<? extends Message>[] messageClasses = new Class[] {
            MessagePing.class,
            MessageHello.class,
            MessageBye.class,
            MessageDisconnect.class,
            MessageAudio.class,
            MessageCall.class,
            MessageHangup.class,
            MessageAcceptCall.class,
            MessageDismissCall.class
    };

    public static Message createMessage(long id, int type) {
        Class<? extends Message> messageClass = messageClasses[type];
        try {
            Constructor<? extends Message> constructor = messageClass.getDeclaredConstructor(long.class, int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(id, type);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
