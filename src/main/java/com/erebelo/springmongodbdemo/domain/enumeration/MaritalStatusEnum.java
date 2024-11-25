package com.erebelo.springmongodbdemo.domain.enumeration;

import com.erebelo.springmongodbdemo.domain.enumeration.type.EnumIdValueType;
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
public enum MaritalStatusEnum implements EnumIdValueType {

    @JsonProperty("Single")
    SINGLE(1, "Single"),

    @JsonProperty("Married")
    MARRIED(2, "Married"),

    @JsonProperty("Divorced")
    DIVORCED(3, "Divorced"),

    @JsonProperty("Widowed")
    WIDOWED(4, "Widowed"),

    @JsonProperty("Domestic Partner")
    DOMESTIC_PARTNERSHIP(5, "Domestic Partner");

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
