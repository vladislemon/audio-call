package net.audiocall.server;

import net.audiocall.Constants;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new Server(getPortFromArgsOrDefault(args)).start();
    }

    private static int getPortFromArgsOrDefault(String[] args) {
        if(args != null) {
            for(String arg : args) {
                try {
                    return Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                    System.out.println("Unknown argument: " + arg);
                    System.out.println("Possible arguments: [port]");
                    System.exit(0);
                }
            }
        }
        return Constants.COMMON_TCP_PORT;
    }
}
