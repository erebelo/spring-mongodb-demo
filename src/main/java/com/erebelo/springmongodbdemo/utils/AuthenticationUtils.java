package com.erebelo.springmongodbdemo.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.erebelo.springmongodbdemo.constants.ProfileConstants.LOGGED_IN_USER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtils {

    public static String getLoggedInUser() {
        return LOGGED_IN_USER;
    }
}
