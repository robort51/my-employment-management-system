package org.example.employmentsystem.config;

import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc 配置
 * 注册 JWT 拦截器，配置哪些接口需要登录，哪些放行
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")           // 拦截所有 /api/ 开头的请求
                .excludePathPatterns(                  // 以下接口不需要登录
                        "/api/user/login",             // 登录
                        "/api/user/register"           // 注册
                );
    }
}
