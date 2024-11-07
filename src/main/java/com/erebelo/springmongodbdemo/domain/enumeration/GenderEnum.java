package com.erebelo.springmongodbdemo.domain.enumeration;

import com.erebelo.springmongodbdemo.domain.enumeration.types.EnumIdType;
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
public enum GenderEnum implements EnumIdType {

    @JsonProperty("Male")
    MALE(1, "Male"),

    @JsonProperty("Female")
    FEMALE(2, "Female"),

    @JsonProperty("Other")
    OTHER(3, "Other");

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
