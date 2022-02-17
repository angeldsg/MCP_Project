package com.mcp.demo.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageStatusEnum {

	DELIVERED("DELIVERED"),
	SEEN("SEEN");

    private final String value;
    private static final Map<String, MessageStatusEnum> CONSTANTS = new HashMap<>();

    static {
        for (MessageStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    MessageStatusEnum(String value) {
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
    public static MessageStatusEnum fromValue(String value) {
    	MessageStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}