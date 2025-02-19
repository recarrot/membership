package com.membership.config;

import com.membership.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                // 允许未认证访问的路径
                .antMatchers("/api/auth/login").permitAll()  // 登录接口
                .antMatchers("/api/auth/register").permitAll() // 如果有注册接口
                .antMatchers("/error").permitAll() // 错误页面
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // API文档相关（如果有使用）
                // 开发环境临时允许所有请求


                .antMatchers("/**").permitAll()  // 添加这行来临时允许所有请求 需要注释


                //.antMatchers("/api/**").authenticated() // 注释掉这行   取消注释
                //.anyRequest().authenticated() // 注释掉这行    取消注释


                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173",
                                "http://localhost:8080",
                                "http://127.0.0.1:8080") // 添加8080端口
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 添加 OPTIONS
                        .allowedHeaders("*") // 允许所有headers
                        .exposedHeaders("Authorization") // 如果需要在响应中暴露某些headers
                        .allowCredentials(true)
                        .maxAge(3600L); // 预检请求的有效期
            }
        };
    }
}