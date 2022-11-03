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
public enum GenderEnum implements EnumTypeId {

    MALE(1, "MALE"),
    FEMALE(2, "FEMALE"),
    OTHER(3, "OTHER");

    private final Integer id;
    private final String value;

    private static final Map<String, GenderEnum> ENUM_MAP;

    static {
        Map<String, GenderEnum> map = new HashMap<>();
        for (GenderEnum instance : GenderEnum.values()) {
            map.put(instance.getValue(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static GenderEnum fromValue(String value) {
        return ENUM_MAP.get(value);
    }
}
