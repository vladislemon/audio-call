package net.audiocall.client.crypt;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellmanAuthState extends DiffieHellmanState {

    private final BigInteger k;
    private final BigInteger m;

    public DiffieHellmanAuthState(BigInteger p, BigInteger g, BigInteger k, BigInteger m, SecureRandom random, int bitLength) {
        super(p, g, random, bitLength);
        this.k = k;
        this.m = m;
    }

    @Override
    public BigInteger getSharedSecret(BigInteger B) {
        BigInteger Akp = A.modPow(k, p);
        BigInteger Bkp = B.modPow(k, p);
        BigInteger gExp = k.multiply(k.subtract(BigInteger.ONE)).multiply(m.modInverse(p));
        BigInteger gExpMod = g.modPow(gExp, p);
        BigInteger BExp = m.multiply(a);
        BigInteger BExpMod = B.modPow(BExp, p);
        return Akp.mod(p)
                .multiply(Bkp.mod(p))
                .multiply(gExpMod.mod(p))
                .multiply(BExpMod.mod(p))
                .mod(p);
    }
}
