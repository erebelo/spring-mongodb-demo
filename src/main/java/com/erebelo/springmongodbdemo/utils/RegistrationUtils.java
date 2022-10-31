package com.erebelo.springmongodbdemo.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationUtils {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "1234567890";

    public static String getRegistrationName() {
        return generateRegistration(10, LETTERS + NUMBERS);
    }

    private static String generateRegistration(int length, String combination) {
        StringBuilder strBuilder = new StringBuilder();
        Random rnd = new Random();

        while (strBuilder.length() < length) {
            int index = (int) (rnd.nextFloat() * combination.length());
            strBuilder.append(combination.charAt(index));
        }

        return strBuilder.toString();
    }
}
