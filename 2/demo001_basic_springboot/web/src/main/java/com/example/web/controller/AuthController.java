package com.example.web.controller;

import com.example.common.dto.LoginDTO;
import com.example.common.vo.LoginVO;
import com.example.common.vo.Result;
import com.example.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、退出、获取当前用户")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名密码登录，返回JWT token")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "清除当前用户的登录状态")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户的信息")
    public Result<LoginVO> getCurrentUser() {
        LoginVO currentUser = authService.getCurrentUser();
        return Result.success(currentUser);
    }
}
