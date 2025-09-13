package com.wlinsk.ansure.controller.user;

import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import com.wlinsk.ansure.model.Result;
import com.wlinsk.ansure.model.dto.user.req.*;
import com.wlinsk.ansure.model.dto.user.resp.QueryUserDetailRespDTO;
import com.wlinsk.ansure.model.dto.user.resp.UserLoginRespDTO;
import com.wlinsk.ansure.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关接口
 *
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 注册
     * @param dto
     * @return
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody UserRegisterReqDTO dto){
        userService.register(dto);
        return Result.ok();
    }

    /**
     * 原生登录
     * @param dto
     * @return
     */
    @PostMapping("/login")
    public Result<UserLoginRespDTO> login(@Validated @RequestBody UserLoginReqDTO dto){
        UserLoginRespDTO result = userService.login(dto);
        return Result.ok(result);
    }

    /**
     * 第三方登录
     * @param dto
     * @return
     */
    @PostMapping("/threePartLogin")
    public Result<String> threePartLogin(@Validated @RequestBody ThreePartLoginReqDTO dto){
        return Result.ok(userService.threePartLogin(dto));
    }

    /**
     * 第三方回调
     * @param code
     * @return
     */
    @PostMapping("/threePartLoginCallback/{code}")
    public Result<UserLoginRespDTO> threePartLoginCallback(@PathVariable("code") String code){
        UserLoginRespDTO userLoginRespDTO = userService.threePartLoginCallback(code);
        return Result.ok(userLoginRespDTO);
    }

    /**
     * 退出
     * @return
     */
    @PostMapping("/logout")
    public Result<Void> logout(){
        userService.logout();
        return Result.ok();
    }

    /**
     * 用户详情
     * @return
     */
    @GetMapping("/queryDetails")
    public Result<QueryUserDetailRespDTO> queryDetails(){
        String userId = BasicAuthContextUtils.getUserId();
        QueryUserDetailRespDTO result = userService.queryById(userId);
        return Result.ok(result);
    }

    /**
     * 修改密码
     * @param dto
     * @return
     */
    @PostMapping("/updatePassword")
    public Result<Void> updatePassword(@Validated @RequestBody UpdatePasswordReqDTO dto){
        userService.updatePassword(dto);
        return Result.ok();
    }

    /**
     * 更新用户名
     * @param dto
     * @return
     */
    @PostMapping("/updateUserName")
    public Result<Void> updateUserName(@Validated @RequestBody UpdateUserNameReqDTO dto){
        userService.updateUserName(dto);
        return Result.ok();
    }

    /**
     * 修改用户头像
     * @param dto
     * @return
     */
    @PostMapping("/updateUserProfile")
    public Result<Void> updateUserProfile(@Validated @RequestBody UpdateUserProfileReqDTO dto){
        userService.updateUserProfile(dto);
        return Result.ok();
    }

    /**
     * 扣除AI积分
     * @param userId
     * @return
     */
    @PostMapping("/reduceAiPoint/{userId}")
    public Result<Void> reduceAiPoint(@PathVariable("userId") String userId){
        userService.reduceAiPoint(userId);
        return Result.ok();
    }

    /**
     * 查询AI积分
     * @param userId
     * @return
     */
    @PostMapping("/queryAiPoint/{userId}")
    public Result<Integer> queryAiPoint(@PathVariable("userId") String userId){
        int result = userService.queryAiPoint(userId);
        return Result.ok(result);
    }

    /**
     * 发送邮箱验证码
     *
     * 现有问题：免费邮件服务器，发送一次要 11s
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/verify")
    public Result<Void> verify(@Validated @RequestBody UserVerifyReqDTO reqDTO){
        userService.verify(reqDTO);
        return Result.ok();
    }
}
