package net.audiocall.client.crypt;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellmanState {

    protected final BigInteger p;
    protected final BigInteger g;
    protected final BigInteger a;
    protected final BigInteger A;

    public DiffieHellmanState(BigInteger p, BigInteger g, SecureRandom random, int bitLength) {
        this.p = p;
        this.g = g;
        this.a = new BigInteger(bitLength, random);
        this.A = g.modPow(a, p);
    }

    public BigInteger getPublicKey() {
        return A;
    }

    public BigInteger getSharedSecret(BigInteger B) {
        return B.modPow(a, p);
    }
}
