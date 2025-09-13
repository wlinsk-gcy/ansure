package com.wlinsk.ansure.basic.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasicException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = -8373281364985709833L;
    private String status;

    private String message;

    public BasicException(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public BasicException(ReturnCode returnCode) {
        this.status = returnCode.getCode();
        this.message = returnCode.getMessage();
    }

    private BasicException(){

    }
}
