package com.wlinsk.ansure.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.user.req.ManagerUpdateRoleReqDTO;
import com.wlinsk.ansure.model.dto.user.req.ManagerUserQueryPageReqDTO;
import com.wlinsk.ansure.model.dto.user.resp.ManagerUserQueryPageRespDTO;
import com.wlinsk.ansure.service.manage.ManagerUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-用户相关接口
 *
 * @Author: wlinsk
 * @Date: 2024/5/26
 */
@RestController
@RequestMapping("/manager/user")
@RequiredArgsConstructor
public class ManagerUserController {

    private final ManagerUserService managerUserService;

    /**
     * 分页查询
     * @param reqDTO
     * @return
     */
    @PostMapping("/queryPage")
    public Result<IPage<ManagerUserQueryPageRespDTO>> queryPage(@Validated @RequestBody ManagerUserQueryPageReqDTO reqDTO){
        IPage<ManagerUserQueryPageRespDTO> result = managerUserService.queryPage(reqDTO);
        return Result.ok(result);
    }

    /**
     * 根据用户id删除
     * @param userId
     * @return
     */
    @PostMapping("/deleteById/{userId}")
    public Result<Void> deleteById(@PathVariable("userId") String userId){
        managerUserService.deleteById(userId);
        return Result.ok();
    }

    /**
     * 修改用户权限
     * @param reqDTO
     * @return
     */
    @PostMapping("/updateRole")
    public Result<Void> updateRole(@Validated @RequestBody ManagerUpdateRoleReqDTO reqDTO){
        managerUserService.updateRole(reqDTO);
        return Result.ok();
    }
}
