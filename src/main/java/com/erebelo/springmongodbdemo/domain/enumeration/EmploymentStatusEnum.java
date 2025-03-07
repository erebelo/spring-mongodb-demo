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
public enum EmploymentStatusEnum implements EnumValueType {

    @JsonProperty("Employed")
    EMPLOYED("Employed"),

    @JsonProperty("Not Employed")
    NOT_EMPLOYED("Not Employed"),

    @JsonProperty("Retired")
    RETIRED("Retired");

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
