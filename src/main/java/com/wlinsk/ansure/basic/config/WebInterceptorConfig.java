package com.wlinsk.ansure.basic.config;

import com.wlinsk.ansure.basic.config.auth.TokenInterceptor;
import com.wlinsk.ansure.basic.log_interceptor.LogIdInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2025/7/6
 */
@Slf4j
@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("add interceptor!!!!!!!!!!!!!!!!");
        registry.addInterceptor(new LogIdInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new TokenInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(getWhiteList());
    }
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        log.info(" addResourceHandlers!!!!!!!!!!!!!!!!");
        // 解决静态资源无法访问
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // 解决swagger无法访问
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 解决swagger的js文件无法访问
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                .allowedHeaders("*")
//                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173", "http://127.0.0.1:8082", "http://127.0.0.1:8083")
                //When allowCredentials is true, allowedOrigins cannot contain the special value "*" since that cannot be set on the "Access-Control-Allow-Origin" response header. To allow credentials to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.
                .allowedOriginPatterns("*")
                .exposedHeaders("token","userId")
                //是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //跨域允许时间
                .maxAge(3600);
    }

    private List<String> getWhiteList() {
        return Arrays.asList(
                "/user/login",
                "/user/threePartLogin",
                "/user/threePartLoginCallback/**",
                "/user/register",
                "/user/verify",
                "/app/queryPage",
                "/app/queryById/**",
                "/doc.html",
                "/v3/api-docs/**",
                "/swagger-ui/index.html/**",
                "/api/swagger-ui/**",
                "/swagger-ui/**",
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                "/","/csrf", "/api-docs", "/api-docs/**","/error");
    }
}
