package com.wlinsk.ansure.basic.config.auth;

import com.wlinsk.ansure.basic.config.holder.SpringContextHolder;
import com.wlinsk.ansure.basic.config.redis.RedisUtils;
import com.wlinsk.ansure.basic.enums.UserRoleEnum;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import com.wlinsk.ansure.basic.utils.BasicAuthContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Slf4j
public class TokenInterceptor implements HandlerInterceptor, Ordered {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //预检请求需要放行,因为预检请求不会携带header,获取代码报错,导致正式请求跨域问题
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        if (pathMatcher.match("/favicon.ico", request.getServletPath())) {
            return false;
        }
        String token = null;
        String accept = request.getHeader("Accept");
        if (StringUtils.isNotBlank(accept) && accept.equals("text/event-stream")){
            token = request.getParameter("token");
            log.info("sse token: {}",token);
        }else {
            token = request.getHeader("token");
        }
        if (StringUtils.isBlank(token)){
            throw new BasicException(SysCode.SYS_TOKEN_EXPIRE);
        }
        RedisUtils redisUtils = SpringContextHolder.getBean(RedisUtils.class);
        String userInfo = redisUtils.getVal(token);
        if (StringUtils.isBlank(userInfo)){
            throw new BasicException(SysCode.SYS_TOKEN_EXPIRE);
        }
        String[] split = userInfo.split(":");
        Optional.ofNullable(split[0])
                .orElseThrow(() -> new BasicException(SysCode.SYS_TOKEN_EXPIRE));
        boolean match = pathMatcher.match("/manager/**", request.getServletPath());
        if (match && !UserRoleEnum.ADMIN.equals(UserRoleEnum.valueOf(split[1]))){
            throw new BasicException(SysCode.SYS_URL_UNAUTHORIZED);
        }
        String contextPath = request.getContextPath();
        BasicAuthContextUtils.init(split[0]);
        log.info("token:{} , contextPath:{}",token,contextPath);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        BasicAuthContextUtils.clearContext();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
