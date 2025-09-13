package com.wlinsk.ansure.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.app.req.ManagerAppQueryPageReqDTO;
import com.wlinsk.ansure.model.dto.app.req.ManagerReviewAddReqDTO;
import com.wlinsk.ansure.model.dto.app.req.ManagerUpdateAppReqDTO;
import com.wlinsk.ansure.model.dto.app.resp.ManagerAppQueryPageRespDTO;
import com.wlinsk.ansure.service.manage.ManagerAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-应用相关
 *
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Slf4j
@RestController
@RequestMapping("/manager/app")
@RequiredArgsConstructor
public class ManagerAppController {

    private final ManagerAppService managerAppService;

    /**
     * 更新应用
     * @param reqDTO
     * @return
     */
    @PostMapping("/update")
    public Result<Void> updateApp(@Validated @RequestBody ManagerUpdateAppReqDTO reqDTO){
        managerAppService.updateApp(reqDTO);
        return Result.ok();
    }

    /**
     * 更新审核
     * @param reqDTO
     * @return
     */
    @PostMapping("/review")
    public Result<Void> reviewApp(@Validated @RequestBody ManagerReviewAddReqDTO reqDTO){
        managerAppService.reviewApp(reqDTO);
        return Result.ok();
    }

    /**
     * 分页查询
     * @param req
     * @return
     */
    @PostMapping("/queryPage")
    public Result<IPage<ManagerAppQueryPageRespDTO>> queryPage(@Validated @RequestBody ManagerAppQueryPageReqDTO req){
        IPage<ManagerAppQueryPageRespDTO> result = managerAppService.queryPage(req);
        return Result.ok(result);
    }

    /**
     * 删除应用
     * @param appId
     * @return
     */
    @PostMapping("/deleteById/{appId}")
    public Result<Void> deleteById(@PathVariable("appId") String appId){
        managerAppService.deleteById(appId);
        return Result.ok();
    }



}
