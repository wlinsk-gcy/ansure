package com.wlinsk.ansure.basic.utils.oss;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/26
 */
@Data
public class UploadRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6072693834792030656L;
    private String imageUrl;
    private String fileName;
}
