package com.wlinsk.ansure.model.dto;

import com.wlinsk.ansure.model.vo.SessionVO;
import lombok.Data;

import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2025/7/7
 */
@Data
public class QuerySessionRecordsRespDTO {

    private String lastSessionId;
    private List<SessionVO> today;
    private List<SessionVO> recentWeek;
    private List<SessionVO> recentMonth;
    private List<SessionVO> recentYear;

}
