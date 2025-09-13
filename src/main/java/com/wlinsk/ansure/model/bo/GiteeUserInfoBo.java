package com.wlinsk.ansure.model.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/8/27
 */
@Data
public class GiteeUserInfoBo implements Serializable {
    @Serial
    private static final long serialVersionUID = -3693002626393876232L;
    private Integer id;
    private String name;
    private String avatar_url;
}
