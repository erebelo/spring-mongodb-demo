package com.erebelo.springmongodbdemo.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BusinessConstants {

    public static final String DEFAULT_SOCKET_TIMEOUT = "5000";
    public static final String DEFAULT_READ_TIMEOUT = "3000";
    public static final String HEALTH_CHECK = "spring-mongodb-demo/healthcheck";
    public static final String PROFILE = "spring-mongodb-demo/profile";
    public static final String WIKIMEDIA = "spring-mongodb-demo/wikimedia";

}