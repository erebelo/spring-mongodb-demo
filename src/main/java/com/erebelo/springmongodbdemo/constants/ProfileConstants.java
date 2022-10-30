package com.erebelo.springmongodbdemo.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfileConstants {

    public static final String LOGGED_IN_USER_ID_HEADER = "X-UserId";
    public static final String REGISTRATION_NAME_HEADER = "X-RegistrationName";

}
