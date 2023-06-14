package net.audiocall.client.crypt;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class DiffieHellmanGenerator {

    private static final int PRIME_CERTAINTY = 100;

    public static BigInteger[] generateNumbersPG(int bitLength, SecureRandom random) {
        BigInteger p, g;
        do {
            p = BigInteger.probablePrime(bitLength, random);
            g = findPrimitive(p);
        } while (g == null);
        return new BigInteger[] { p, g };
    }

    public static byte[] getNumberBytes(BigInteger n) {
        byte[] bytes = n.toByteArray();
        byte[] result = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, result, 0, result.length);
        return result;
    }

    private static BigInteger findPrimitive(BigInteger n) {
        if (!n.isProbablePrime(PRIME_CERTAINTY)) {
            return null;
        }
        BigInteger phi = n.divide(BigInteger.ONE);
        Set<BigInteger> primeFactors = findPrimeFactors(phi);
        for (BigInteger r = BigInteger.TWO; r.compareTo(phi) < 1; r = r.add(BigInteger.ONE)) {
            boolean flag = false;
            for (BigInteger a : primeFactors) {
                if(r.modPow(phi.divide(a), n).equals(BigInteger.ONE)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return r;
            }
        }
        return null;
    }

    private static Set<BigInteger> findPrimeFactors(BigInteger n) {
        Set<BigInteger> factors = new HashSet<>();
        while (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            factors.add(BigInteger.TWO);
            n = n.divide(BigInteger.TWO);
        }
        for (BigInteger i = BigInteger.valueOf(3); i.compareTo(n.sqrt()) < 1; n = n.add(BigInteger.TWO)) {
            while (n.mod(i).equals(BigInteger.ZERO)) {
                factors.add(i);
                n = n.divide(i);
            }
        }
        if (n.compareTo(BigInteger.TWO) > 0) {
            factors.add(n);
        }
        return factors;
    }
}
