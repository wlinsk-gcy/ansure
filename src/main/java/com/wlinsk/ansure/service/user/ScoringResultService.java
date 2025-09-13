package com.wlinsk.ansure.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wlinsk.ansure.model.dto.scoringResult.req.AddScoringResultReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.req.QueryScoringResultPageReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.req.UpdateScoringResultReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.resp.QueryScoringResultPageRespDTO;
import com.wlinsk.ansure.model.entity.ScoringResult;

/**
 *
 */
public interface ScoringResultService extends IService<ScoringResult> {

    void addScoringResult(AddScoringResultReqDTO reqDTO);

    void updateScoringResult(UpdateScoringResultReqDTO reqDTO);

    IPage<QueryScoringResultPageRespDTO> queryPage(QueryScoringResultPageReqDTO reqDTO);

    void deleteById(String resultId);

}
