package com.membership.service;

import com.membership.dto.JwtResponse;
import com.membership.dto.LoginRequest;
import com.membership.entity.User;
import com.membership.exception.BusinessException;
import com.membership.repository.UserRepository;
import com.membership.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public JwtResponse login(LoginRequest request) {
        log.info("Processing login request for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found: {}", request.getUsername());
                    return new BusinessException("用户名或密码错误");
                });
        log.info("Input password: {}", request.getPassword());
        log.info("Stored password: {}"
                , user.getPassword());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Password mismatch for user: {}", request.getUsername());
            throw new BusinessException("用户名或密码错误");
        }


        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", ""))
                .build();

        log.info("创建UserDetails - 用户名: {}, 角色: {}", user.getUsername(), user.getRole());
        String token = jwtService.generateToken(userDetails);
        log.info("生成JWT token成功 - 用户名: {}, token: {}", request.getUsername(), token);

        return JwtResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();
    }
}