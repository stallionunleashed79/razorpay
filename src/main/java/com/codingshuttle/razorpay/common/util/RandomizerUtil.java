package com.codingshuttle.razorpay.common.util;

import java.security.SecureRandom;

public class RandomizerUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String randomBase64(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
