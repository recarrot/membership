package com.membership.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // 不需要过滤的路径
    private final List<String> excludedPaths = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/login",
            "/members",
            "/index.html",
            "/static",
            "/js",
            "/css",
            "/img",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("处理请求: {} {}", request.getMethod(), path);

        if (shouldNotFilter(request)) {
            log.info("跳过JWT验证的路径: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            log.info("提取到JWT token");
            try {
                username = jwtUtil.extractUsername(jwt);
                log.info("从JWT中提取的用户名: {}", username);
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token已过期: {}", e.getMessage());
            } catch (Exception e) {
                log.error("JWT Token解析错误: {}", e.getMessage());
            }
        } else {
            log.warn("未找到有效的Authorization头");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            log.info("加载到用户详情: {}", username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JWT验证成功，已设置安全上下文");
            } else {
                log.warn("JWT验证失败");
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean shouldNotFilter = excludedPaths.stream().anyMatch(path::startsWith) ||
                path.contains(".") || // 静态资源通常包含点号
                request.getMethod().equals("OPTIONS"); // 预检请求
        
        if (shouldNotFilter) {
            log.info("请求 {} 不需要JWT验证", path);
        }
        
        return shouldNotFilter;
    }
}