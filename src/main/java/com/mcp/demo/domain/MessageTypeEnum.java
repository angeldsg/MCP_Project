package com.mcp.demo.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageTypeEnum {

    CALL("CALL"),
    MSG("MSG");

    private final String value;
    private static final Map<String, MessageTypeEnum> CONSTANTS = new HashMap<>();

    static {
        for (MessageTypeEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    MessageTypeEnum(String value) {
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
    public static MessageTypeEnum fromValue(String value) {
    	MessageTypeEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}