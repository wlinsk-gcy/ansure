package com.wlinsk.ansure.basic.enums;

import lombok.Getter;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
@Getter
public enum MessageTypeEnum {
    USER(1, "用户提问"),
    ASSISTANT(2, "AI的回答");

    private final int value;
    private final String desc;

    MessageTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String toString() {
        return this.name();
    }
}
