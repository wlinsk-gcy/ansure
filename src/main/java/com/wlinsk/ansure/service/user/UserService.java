package com.wlinsk.ansure.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wlinsk.ansure.model.dto.user.req.*;
import com.wlinsk.ansure.model.dto.user.resp.QueryUserDetailRespDTO;
import com.wlinsk.ansure.model.dto.user.resp.UserLoginRespDTO;
import com.wlinsk.ansure.model.entity.User;

/**
 *
 */
public interface UserService extends IService<User> {

    void register(UserRegisterReqDTO dto);

    UserLoginRespDTO login(UserLoginReqDTO dto);

    QueryUserDetailRespDTO queryById(String userId);

    void logout();

    String threePartLogin(ThreePartLoginReqDTO dto);

    UserLoginRespDTO threePartLoginCallback(String code);

    void updatePassword(UpdatePasswordReqDTO dto);

    void updateUserName(UpdateUserNameReqDTO dto);

    void updateUserProfile(UpdateUserProfileReqDTO dto);

    void reduceAiPoint(String userId);

    int queryAiPoint(String userId);

    void verify(UserVerifyReqDTO reqDTO);
}
