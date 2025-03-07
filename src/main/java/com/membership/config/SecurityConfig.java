package com.membership.config;

import com.membership.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                // 静态资源和登录页面允许访问
                .antMatchers("/", "/login", "/index.html", "/static/**", "/js/**", "/css/**", "/img/**", "/members").permitAll()
                // API认证端点允许匿名访问
                .antMatchers("/api/auth/login", "/api/auth/register").permitAll()
                // 错误页面允许访问
                .antMatchers("/error").permitAll()
                // Swagger文档允许访问（如果需要）
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 其他API需要认证
                .antMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .cors()
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
}