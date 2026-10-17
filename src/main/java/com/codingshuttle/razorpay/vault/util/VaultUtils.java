package com.codingshuttle.razorpay.vault.util;

import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;

public class VaultUtils {

    public static BytesEncryptor panEncryptor(byte[] deck) {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(deck, "AES");
        return new AesBytesEncryptor(secretKeySpec, KeyGenerators.secureRandom(12),
                AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
