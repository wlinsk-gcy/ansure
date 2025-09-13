package com.wlinsk.ansure.basic.enums;

import lombok.Getter;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
@Getter
public enum ChatEventTypeEnum {

    DATA(1001, "数据事件"),
    STOP(1002, "停止事件"),
    PARAM(1003, "参数事件"),
    ERROR(1004, "异常事件");

    private final int value;
    private final String desc;

    ChatEventTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String toString() {
        return this.name();
    }
}
