package com.wlinsk.ansure.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.app.req.*;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppDetailsRespDTO;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppListRespDTO;
import com.wlinsk.ansure.model.dto.app.resp.QueryAppPageRespDTO;
import com.wlinsk.ansure.service.user.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用相关
 *
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@RestController
@RequestMapping("/app")
@Tag(name = "应用相关")
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;

    /**
     * 添加应用
     * @param dto
     * @return
     */
    @Operation(summary = "添加应用",description = "添加应用信息")
    @PostMapping("/add")
    public Result<String> addApp(@Validated @RequestBody AddAppReqDTO dto){
        String appId = appService.addApp(dto);
        return Result.ok(appId);
    }

    /**
     * 修改应用
     * @param reqDTO
     * @return
     */
    @PostMapping("/update")
    public Result<Void> updateApp(@Validated @RequestBody UpdateAppReqDTO reqDTO){
        appService.updateApp(reqDTO);
        return Result.ok();
    }

    /**
     * 删除应用
     * @param reqDTO
     * @return
     */
    @PostMapping("/delete")
    public Result<Void> deleteApp(@Validated @RequestBody DeleteAppReqDTO reqDTO){
        appService.deleteApp(reqDTO);
        return Result.ok();
    }

    /**
     * 分页获取应用列表
     * @param reqDTO
     * @return
     */
    @PostMapping("/queryPage")
    public Result<IPage<QueryAppPageRespDTO>> queryPage(@Validated @RequestBody QueryAppPageReqDTO reqDTO){
        IPage<QueryAppPageRespDTO> result = appService.queryPage(reqDTO);
        return Result.ok(result);
    }

    /**
     * 固定获取5条符合条件的应用信息
     * @param reqDTO
     * @return
     */
    @PostMapping("/queryList")
    public Result<List<QueryAppListRespDTO>> queryList(@Validated @RequestBody QueryAppListReqDTO reqDTO){
        List<QueryAppListRespDTO> result = appService.queryList(reqDTO);
        return Result.ok(result);
    }

    /**
     * 查询最新的可用应用信息
     * @return
     */
    @PostMapping("/queryLast")
    public Result<QueryAppListRespDTO> queryLast(){
        QueryAppListRespDTO result = appService.queryLast();
        return Result.ok(result);
    }

    /**
     * 根据id获取应用详情
     * @param appId
     * @return
     */
    @PostMapping("/queryById/{appId}")
    public Result<QueryAppDetailsRespDTO> queryById(@PathVariable("appId")String appId){
        QueryAppDetailsRespDTO respDTO = appService.queryById(appId);
        return Result.ok(respDTO);
    }

    /**
     * 分页查询自己的应用
     * @param reqDTO
     * @return
     */
    @PostMapping("/queryMyPage")
    public Result<IPage<QueryAppPageRespDTO>> queryMyPage(@Validated @RequestBody QueryMyAppPageReqDTO reqDTO){
        IPage<QueryAppPageRespDTO> result = appService.queryMyPage(reqDTO);
        return Result.ok(result);
    }
}
