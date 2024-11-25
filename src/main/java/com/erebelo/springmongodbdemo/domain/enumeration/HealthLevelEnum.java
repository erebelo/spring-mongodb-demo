package com.erebelo.springmongodbdemo.domain.enumeration;

import com.erebelo.springmongodbdemo.domain.enumeration.type.EnumCodeValueType;
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
public enum HealthLevelEnum implements EnumCodeValueType {

    @JsonProperty("Below average")
    BELOW_AVERAGE("belowAverage", "Below average"),

    @JsonProperty("Average")
    AVERAGE("average", "Average"),

    @JsonProperty("Above average")
    ABOVE_AVERAGE("aboveAverage", "Above average");

    private static final Map<String, HealthLevelEnum> ENUM_MAP;

    static {
        Map<String, HealthLevelEnum> map = new HashMap<>();
        for (HealthLevelEnum instance : HealthLevelEnum.values()) {
            map.put(instance.getCode(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    private final String code;
    private final String value;

    public static HealthLevelEnum fromCode(String code) {
        return ENUM_MAP.get(code);
    }
}
