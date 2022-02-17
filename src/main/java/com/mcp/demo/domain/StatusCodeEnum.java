package com.mcp.demo.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusCodeEnum {

    OK("OK"),
    KO("KO");

    private final String value;
    private static final Map<String, StatusCodeEnum> CONSTANTS = new HashMap<>();

    static {
        for (StatusCodeEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    StatusCodeEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static StatusCodeEnum fromValue(String value) {
    	StatusCodeEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}