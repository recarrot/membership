// AuthController.java
package com.membership.controller;

import com.membership.dto.LoginRequest;
import com.membership.dto.JwtResponse;
import com.membership.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for username: {}", request.getUsername());
        try {
            JwtResponse response = authService.login(request);
            log.info("登录成功 - 用户名: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("登录失败 - 用户名: {}, 错误: {}", request.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
}
// MemberController.java
