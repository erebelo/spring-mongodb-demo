package com.erebelo.springmongodbdemo.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HashAlgorithmUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashAlgorithmUtils.class);

    private static final MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error initializing the MessageDigest instance", e);
        }
    }

    public static synchronized String generateSHAHashObject(String objString) {
        StringBuilder objStringBuilder = new StringBuilder(objString);
        objStringBuilder.append(String.format(", currentTimeMillis=%s", System.currentTimeMillis()));
        LOGGER.info("Generating a hash object through object: {}", objStringBuilder);
        byte[] hash = digest.digest(objStringBuilder.toString().getBytes(StandardCharsets.UTF_8));

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
