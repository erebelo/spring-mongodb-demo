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
public enum EmploymentStatusEnum {

    EMPLOYED("EMPLOYED"),
    NOT_EMPLOYED("NOT_EMPLOYED"),
    RETIRED("RETIRED");

    private final String value;

    private static final Map<String, EmploymentStatusEnum> ENUM_MAP;

    static {
        Map<String, EmploymentStatusEnum> map = new HashMap<>();
        for (EmploymentStatusEnum instance : EmploymentStatusEnum.values()) {
            map.put(instance.getValue(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static EmploymentStatusEnum fromValue(String value) {
        return ENUM_MAP.get(value);
    }
}
