package com.erebelo.springmongodbdemo.context.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@AllArgsConstructor
public enum HistoryActionEnum {

    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String value;
    private static final Map<String, HistoryActionEnum> ENUM_MAP;

    static {
        Map<String, HistoryActionEnum> map = new HashMap<>();
        for (HistoryActionEnum instance : HistoryActionEnum.values()) {
            map.put(instance.getValue(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static HistoryActionEnum fromValue(String value) {
        return ENUM_MAP.get(value);
    }
}
