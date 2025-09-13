package com.wlinsk.ansure.model.dto.scoringResult.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2024/5/29
 */
@Data
public class AddScoringResultReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2691749575935205123L;
    /**
     * 结果名称，如物流师
     */
    @NotBlank(message = "结果名称不能为空")
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 结果图片
     */
    private String resultPicture;

    /**
     * 结果属性集合 JSON，如 [I,S,T,J]
     */
    private List<String> resultProp;

    /**
     * 结果得分范围，如 80，表示 80及以上的分数命中此结果
     */
    private Integer resultScoreRange;
    /**
     * 应用id
     */
    @NotBlank(message = "应用id不能为空")
    private String appId;
}
