package com.senjay.archat.common.config;

import com.senjay.archat.common.interceptor.BaseInterceptor;
import com.senjay.archat.common.interceptor.TokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;




@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
//    注入拦截器
    @Resource
    private TokenInterceptor tokenInterceptor;
    @Resource
    private BaseInterceptor baseInterceptor;

//    这里的顺序 和 @order 定义单独顺序有什么区别
    @Override
//    注册白名单
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor)
                .excludePathPatterns(
                        "/client/user/login",
                        "/admin/user/login",
                        "/admin/user/register",
                        "/client/user/register"

                );
        registry.addInterceptor(tokenInterceptor)
                .excludePathPatterns(
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-ui.html",
                        "/favicon.ico",
                        "/client/user/login",
                        "/admin/user/login",
                        "/admin/user/register",
                        "/client/user/register",
                        "/client/visit/count",
                        "/public/**"
                );

    }

}
