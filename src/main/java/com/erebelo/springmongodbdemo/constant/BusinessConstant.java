package com.erebelo.springmongodbdemo.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BusinessConstant {

    public static final String REQUEST_ID_HEADER = "RequestID";
    public static final String GLOBAL_EXCEPTION_MESSAGE = "An error occurred for RequestID='{}': {}";
    public static final String LINE_DELIMITERS = "[\\r\\n\\t]+";
    public static final String MERGE_PATCH_MEDIA_TYPE = "application/merge-patch+json";
    public static final String HEALTH_CHECK_PATH = "/health-check";
    public static final String PROFILES_PATH = "/profiles";
    public static final String WIKIMEDIA_PATH = "/wikimedia";
    public static final String ARTICLES_PATH = "/articles";
    public static final String FILES_PATH = "/files";
    public static final String ADDRESSES_PATH = "/addresses";
    public static final String ADDRESSES_BULK_PATH = "/bulk";

}
