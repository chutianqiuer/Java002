package com.example.web.controller;

import com.example.common.dto.PageDTO;
import com.example.common.dto.UserDTO;
import com.example.common.vo.PageVO;
import com.example.common.vo.Result;
import com.example.common.vo.UserVO;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户注册、查询、修改、删除")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public Result<Long> register(@Valid @RequestBody UserDTO userDTO) {
        return Result.success(userService.register(userDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户信息")
    public Result<UserVO> getById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "用户分页列表", description = "分页查询用户列表")
    public Result<PageVO<UserVO>> getPage(PageDTO pageDTO) {
        return Result.success(userService.getPage(pageDTO));
    }

    @PutMapping
    @Operation(summary = "更新用户", description = "修改用户信息")
    public Result<Void> update(@Valid @RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    public Result<Void> delete(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "修改用户状态", description = "启用或禁用用户")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
}
