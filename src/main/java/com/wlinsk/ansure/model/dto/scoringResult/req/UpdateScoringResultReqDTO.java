package com.wlinsk.ansure.model.dto.scoringResult.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: wlinsk
 * @Date: 2024/5/29
 */
@Data
public class UpdateScoringResultReqDTO extends AddScoringResultReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7925721671234676722L;
    @NotBlank(message = "评分结果id不可为空")
    private String resultId;

}
