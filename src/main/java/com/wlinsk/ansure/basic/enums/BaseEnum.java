package com.wlinsk.ansure.basic.enums;

/**
 * @Author: wlinsk
 * @Date: 2024/4/21
 */
public interface BaseEnum<E extends Enum<?>, T> {
    T getCode();

    String getMessage();
}
