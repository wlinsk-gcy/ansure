package com.wlinsk.ansure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlinsk.ansure.basic.enums.UserRoleEnum;
import com.wlinsk.ansure.model.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.wlinsk.model.entity.User
 */
public interface UserMapper extends BaseMapper<User> {

    User queryByUserAccount(@Param("userAccount") String userAccount);

    User queryByUserId(@Param("userId") String userId);
    IPage<User> queryUserPage(Page<User> page, @Param("userId") String userId,
                              @Param("userAccount") String userAccount, @Param("userName") String userName,
                              @Param("userRole") UserRoleEnum userRole);

    List<User> queryByUserIdList(@Param("userIdList") List<String> userIdList);

    int deleteUser(@Param("user") User user);

    int updatePassword(@Param("user")User user);

    int updateUserName(@Param("user")User user);

    int updateUserProfile(@Param("user")User user);

    int updateUserRole(@Param("user")User user);

    int reduceAiPoint(@Param("aiPoint") Integer aiPoint, @Param("userId") String userId, @Param("version") Long version);

    int resetUserAiPoint();
}




