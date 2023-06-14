package net.audiocall.client.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class FileUtil {

    public static BigInteger readBigInteger(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            BigInteger result = new BigInteger(reader.readLine());
            reader.close();
            return result;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            try {
                reader.close();
            } catch (IOException e1) {
                //
            }
            return null;
        }
    }
}
