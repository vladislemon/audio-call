package net.audiocall.client.util;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class SwingUtil {

    public static void runInUIThread(Runnable runnable) {
        if(SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
