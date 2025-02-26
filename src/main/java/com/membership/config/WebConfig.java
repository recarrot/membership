package com.membership.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 处理前端路由刷新问题的控制器
     * 使用多个简单匹配而非复杂正则表达式
     */
    @Controller
    public static class RouteController {
        // 主路径和一级路径匹配
        @GetMapping(value = {"/", "/{path:[^\\.]*}"})
        public String forwardRoot() {
            return "forward:/index.html";
        }

        // 排除api和swagger开头的路径，其他多级路径匹配
        @GetMapping(value = {"/app/**", "/user/**", "/dashboard/**", "/profile/**", "/settings/**", "/admin/**"})
        public String forwardSubPaths() {
            return "forward:/index.html";
        }
    }
}