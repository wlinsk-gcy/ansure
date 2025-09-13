package com.wlinsk.ansure.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.scoringResult.req.AddScoringResultReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.req.QueryScoringResultPageReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.req.UpdateScoringResultReqDTO;
import com.wlinsk.ansure.model.dto.scoringResult.resp.QueryScoringResultPageRespDTO;
import com.wlinsk.ansure.service.user.ScoringResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 评分结果相关
 *
 * @Author: wlinsk
 * @Date: 2024/5/29
 */
@RestController
@RequestMapping("/scoringResult")
@RequiredArgsConstructor
public class ScoringResultController {
    private final ScoringResultService scoringResultService;

    /**
     * 添加评分策略
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/add")
    public Result<Void> addScoringResult(@Validated @RequestBody AddScoringResultReqDTO reqDTO) {
        scoringResultService.addScoringResult(reqDTO);
        return Result.ok();
    }

    /**
     * 修改评分策略
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/update")
    public Result<Void> updateScoringResult(@Validated @RequestBody UpdateScoringResultReqDTO reqDTO) {
        scoringResultService.updateScoringResult(reqDTO);
        return Result.ok();
    }

    /**
     * 分页查询评分策略
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/queryPage")
    public Result<IPage<QueryScoringResultPageRespDTO>> queryPage(@Validated @RequestBody QueryScoringResultPageReqDTO reqDTO) {
        IPage<QueryScoringResultPageRespDTO> result = scoringResultService.queryPage(reqDTO);
        return Result.ok(result);
    }

    /**
     * 根据id删除评分策略
     *
     * @param resultId
     * @return
     */
    @PostMapping("/deleteById/{resultId}")
    public Result<Void> deleteById(@PathVariable("resultId") String resultId) {
        scoringResultService.deleteById(resultId);
        return Result.ok();
    }
}
