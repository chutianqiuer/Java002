package com.example.web.controller;

import com.example.common.dto.LoginDTO;
import com.example.common.vo.LoginVO;
import com.example.common.vo.Result;
import com.example.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/current")
    public Result<LoginVO> getCurrentUser() {
        LoginVO currentUser = authService.getCurrentUser();
        return Result.success(currentUser);
    }
}
