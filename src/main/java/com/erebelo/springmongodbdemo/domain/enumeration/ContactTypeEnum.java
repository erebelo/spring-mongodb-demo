package com.erebelo.springmongodbdemo.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@AllArgsConstructor
public enum ContactTypeEnum {

    PHONE("PHONE"),
    EMAIL("EMAIL");

    private final String value;

    private static final Map<String, ContactTypeEnum> ENUM_MAP;

    static {
        Map<String, ContactTypeEnum> map = new HashMap<>();
        for (ContactTypeEnum instance : ContactTypeEnum.values()) {
            map.put(instance.getValue(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static ContactTypeEnum fromValue(String value) {
        return ENUM_MAP.get(value);
    }
}
