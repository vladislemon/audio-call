package net.audiocall;

public class Constants {

    public static final int COMMON_TCP_PORT = 8914;

    public static final String CLIENT_TITLE = "Audio Call";
    public static final boolean CLIENT_WINDOW_IS_RESIZEABLE = false;
    public static final String CLIENT_EXCEPTION_DIALOG_TITLE = "Exception";
    public static final int CLIENT_TAB_HEIGHT = 24;
    public static final int CLIENT_NAME_MAX_LENGTH = 32;
    public static final float CLIENT_USER_INFO_FONT_SIZE = 12F;
    public static final int CLIENT_AUDIO_BUFFER_SIZE = 19200;
    public static final int CLIENT_AUDIO_LINE_BUFFER_SIZE = 19200 * 32;
    public static final int CLIENT_AUDIO_FORMAT_SAMPLE_RATE = 48000;
    public static final int CLIENT_AUDIO_FORMAT_SAMPLE_SIZE = 16;
    public static final int CLIENT_AUDIO_FORMAT_CHANNELS = 1;
    public static final int CLIENT_AUDIO_FORMAT_FRAME_SIZE = 2;
    public static final int CLIENT_AUDIO_FORMAT_FRAME_RATE = 48000;
    public static final boolean CLIENT_AUDIO_FORMAT_BIG_ENDIAN = true;
    public static final String CLIENT_UNTRUSTED_TITLE = "Untrusted user";
    public static final String CLIENT_ICON_FILE_NAME = "icon.png";
    public static final String CLIENT_RESOURCE_BUNDLE_NAME = "Application";

    public static final int CLIENT_DH_P_BIT_LENGTH = 1024;
    public static final int CLIENT_DH_KEY_BIT_LENGTH = 1024;
    public static final String CLIENT_DH_NUMBERS_PATH = "users";

    public static final int SERVER_CALL_DIALING_TIMEOUT = 20000;
}
