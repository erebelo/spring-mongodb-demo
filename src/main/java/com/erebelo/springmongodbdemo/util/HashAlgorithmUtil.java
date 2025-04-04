package com.erebelo.springmongodbdemo.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class HashAlgorithmUtil {

    private static final MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error initializing the MessageDigest instance", e);
        }
    }

    public static synchronized String generateSHAHashObject(String objString) {
        String newObjString = objString + ", currentTimeMillis=" + System.currentTimeMillis();

        log.info("Generating a hash value by the provided object");
        byte[] hash = digest.digest(newObjString.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
