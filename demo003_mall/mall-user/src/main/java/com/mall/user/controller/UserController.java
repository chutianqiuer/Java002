package com.mall.user.controller;

import com.mall.common.entity.User;
import com.mall.common.response.ApiResponse;
import com.mall.common.response.PageResponse;
import com.mall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody User user) {
        User registered = userService.register(user);
        registered.setPassword(null);
        return ApiResponse.success(registered);
    }

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        user.setPassword(null);
        return ApiResponse.success(user);
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return ApiResponse.success(user);
    }

    @GetMapping("/username/{username}")
    public ApiResponse<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return ApiResponse.success(user);
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<User>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResponse.of(
                userService.countUsers(),
                page,
                size,
                userService.listUsers(page, size)
        ));
    }
}
