package net.audiocall.client.crypt;

public class NASHCipher {

    public static final int ROUND_COUNT = 24;

    private static final int[] shiftTable = {
            11, 14, 10, 19
    };

    private static final int[] primes = {
            2, 3, 5, 7
    };

    private final int[] initialKey;

    public NASHCipher(byte[] sharedSecret) {
        this.initialKey = generateInitialKey(toIntegers(sharedSecret));
    }

    public long encodeMessage(long message) {
        for(int i = 0; i < ROUND_COUNT; i++) {
            int key = generateRoundKey(initialKey, i);
            message = round(message, key);
        }
        message = (message >>> 32) | (message << 32);
        return message;
    }

    //128-bit shared secret (4 integers)
    private static int[] generateInitialKey(int[] sharedSecret) {
        int[] key = new int[8];
        System.arraycopy(sharedSecret, 0, key, 0, 4);
        for(int i = 0; i < 4; i++) {
            key[i + 4] = (int) ((Double.doubleToRawLongBits(Math.sqrt(primes[i])) & 0xFFFFFFFF00000L) >> 20);
        }
        return key;
    }

    private static int generateRoundKey(int[] initialKey, int round) {
        long data = ((long)initialKey[0]) << 32 | initialKey[1];
        int key = initialKey[((round + 2) % 6) + 2] ^ (round + 2);
        data = round(data, key);
        return (int) (data >>> 32);
    }

    private static long round(long data, int key) {
        long R = data & 0xFFFFFFFFL;
        long L = (data & 0xFFFFFFFF00000000L) >>> 32;
        long S = (L + key);
        int A = (int) ((L >>> S) & 1);
        int B = (int) ((S >>> (L & 0b11111)) & 1);
        int AB = A << 1 | B;
        int shift = shiftTable[AB];
        long shiftS = (S >>> shift) | (S << (Integer.SIZE - shift));
        long newL = shiftS ^ R;
        return (newL << 32) | L;
    }

    public long decodeMessage(long message) {
        for(int i = ROUND_COUNT - 1; i >= 0; i--) {
            int key = generateRoundKey(initialKey, i);
            message = round(message, key);
        }
        message = (message >>> 32) | (message << 32);
        return message;
    }

    public void encodeBytes(byte[] bytes, int offset, int length) {
        processBytes(bytes, offset, length, true);
    }

    public void decodeBytes(byte[] bytes, int offset, int length) {
        processBytes(bytes, offset, length, false);
    }

    private void processBytes(byte[] bytes, int offset, int length, boolean encode) {
        for(int i = offset/8; i < (offset + length) / 8; i++) {
            long message =
                    Byte.toUnsignedLong(bytes[i*8  ]) << 56 |
                    Byte.toUnsignedLong(bytes[i*8+1]) << 48 |
                    Byte.toUnsignedLong(bytes[i*8+2]) << 40 |
                    Byte.toUnsignedLong(bytes[i*8+3]) << 32 |
                    Byte.toUnsignedLong(bytes[i*8+4]) << 24 |
                    Byte.toUnsignedLong(bytes[i*8+5]) << 16 |
                    Byte.toUnsignedLong(bytes[i*8+6]) << 8 |
                    Byte.toUnsignedLong(bytes[i*8+7]);
            long processed = encode ? encodeMessage(message) : decodeMessage(message);
            bytes[i*8  ] = (byte) ((processed >>> 56) & 0xFF);
            bytes[i*8+1] = (byte) ((processed >>> 48) & 0xFF);
            bytes[i*8+2] = (byte) ((processed >>> 40) & 0xFF);
            bytes[i*8+3] = (byte) ((processed >>> 32) & 0xFF);
            bytes[i*8+4] = (byte) ((processed >>> 24) & 0xFF);
            bytes[i*8+5] = (byte) ((processed >>> 16) & 0xFF);
            bytes[i*8+6] = (byte) ((processed >>> 8) & 0xFF);
            bytes[i*8+7] = (byte) ( processed & 0xFF);
        }
    }

    private static int[] toIntegers(byte[] bytes) {
        int[] result = new int[bytes.length / 4];
        for(int i = 0; i < result.length; i++) {
            result[i] = bytes[i * 4    ] << 24
                      | bytes[i * 4 + 1] << 16
                      | bytes[i * 4 + 2] << 8
                      | bytes[i * 4 + 3];
        }
        return result;
    }
}
