package com.erebelo.springmongodbdemo.domain.enumeration;

import com.erebelo.springmongodbdemo.domain.enumeration.type.EnumValueType;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContactTypeEnum implements EnumValueType {

    @JsonProperty("Phone")
    PHONE("Phone"),

    @JsonProperty("Email")
    EMAIL("Email");

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
