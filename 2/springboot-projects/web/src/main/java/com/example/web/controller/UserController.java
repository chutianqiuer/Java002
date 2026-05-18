package com.example.web.controller;

import com.example.common.dto.PageDTO;
import com.example.common.dto.UserDTO;
import com.example.common.vo.PageVO;
import com.example.common.vo.Result;
import com.example.common.vo.UserVO;
import com.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody UserDTO userDTO) {
        return Result.success(userService.register(userDTO));
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/page")
    public Result<PageVO<UserVO>> getPage(PageDTO pageDTO) {
        return Result.success(userService.getPage(pageDTO));
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
}
