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
public enum MaritalStatusEnum {

    SINGLE(1, "SINGLE"),
    MARRIED(2, "MARRIED"),
    DIVORCED(3, "DIVORCED"),
    WIDOWED(4, "WIDOWED"),
    DOMESTIC_PARTNERSHIP(5, "DOMESTIC_PARTNERSHIP");

    private final Integer id;
    private final String value;
    private static final Map<String, MaritalStatusEnum> ENUM_MAP;

    static {
        Map<String, MaritalStatusEnum> map = new HashMap<>();
        for (MaritalStatusEnum instance : MaritalStatusEnum.values()) {
            map.put(instance.getValue(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static MaritalStatusEnum fromValue(String value) {
        return ENUM_MAP.get(value);
    }
}
