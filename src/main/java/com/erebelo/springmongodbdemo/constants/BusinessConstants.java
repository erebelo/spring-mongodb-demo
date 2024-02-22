package com.erebelo.springmongodbdemo.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BusinessConstants {

    public static final String DEFAULT_CONNECTION_TIMEOUT = "3000";
    public static final String DEFAULT_SOCKET_TIMEOUT = "5000";
    public static final String MERGE_PATCH_MEDIA_TYPE = "application/merge-patch+json";
    public static final String HEALTH_CHECK = "/healthcheck";
    public static final String PROFILE = "/profile";
    public static final String WIKIMEDIA = "/wikimedia";
    public static final String ARTICLES = "/articles";

}