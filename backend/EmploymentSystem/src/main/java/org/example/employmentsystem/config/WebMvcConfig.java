package org.example.employmentsystem.config;

import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc 配置
 * 注册 JWT 拦截器，配置静态资源映射
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 的请求映射到本地 uploads/ 目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
