package com.wlinsk.ansure.model.dto.scoringResult.resp;

import com.wlinsk.ansure.model.dto.user.resp.QueryUserDetailRespDTO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2024/5/29
 */
@Data
public class QueryScoringResultPageRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3669834689024334496L;
    /**
     * 结果id
     */
    private String resultId;

    /**
     * 结果名称，如物流师
     */
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
    private String appId;

    /**
     * 用户id
     */
    private String userId;
    private Date createTime;
    private Date updateTime;
    private QueryUserDetailRespDTO userInfo;
}
