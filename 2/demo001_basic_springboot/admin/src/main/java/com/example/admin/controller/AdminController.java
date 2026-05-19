package com.example.admin.controller;

import com.example.admin.dto.AdminOrderDTO;
import com.example.admin.dto.AdminProductDTO;
import com.example.admin.dto.AdminUserDTO;
import com.example.common.vo.OrderVO;
import com.example.common.vo.PageVO;
import com.example.common.vo.ProductVO;
import com.example.common.vo.Result;
import com.example.common.vo.UserVO;
import com.example.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api")
@Tag(name = "后台管理", description = "后台用户、商品、订单管理")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ========== User Management ==========
    @GetMapping("/user/page")
    @Operation(summary = "用户分页列表", description = "后台分页查询用户")
    public Result<PageVO<UserVO>> getUserPage(AdminUserDTO adminUserDTO) {
        return Result.success(adminService.getUserPage(adminUserDTO));
    }

    @PostMapping("/user")
    @Operation(summary = "创建用户", description = "后台创建新用户")
    public Result<Void> createUser(@RequestBody AdminUserDTO adminUserDTO) {
        adminService.createUser(adminUserDTO);
        return Result.success();
    }

    @PutMapping("/user")
    @Operation(summary = "更新用户", description = "后台更新用户信息")
    public Result<Void> updateUser(@RequestBody AdminUserDTO adminUserDTO) {
        adminService.updateUser(adminUserDTO);
        return Result.success();
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "删除用户", description = "后台删除用户")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success();
    }

    // ========== Product Management ==========
    @GetMapping("/product/page")
    @Operation(summary = "商品分页列表", description = "后台分页查询商品")
    public Result<PageVO<ProductVO>> getProductPage(AdminProductDTO adminProductDTO) {
        return Result.success(adminService.getProductPage(adminProductDTO));
    }

    @PostMapping("/product")
    @Operation(summary = "创建商品", description = "后台创建新商品")
    public Result<Long> createProduct(@RequestBody AdminProductDTO adminProductDTO) {
        return Result.success(adminService.createProduct(adminProductDTO));
    }

    @PutMapping("/product")
    @Operation(summary = "更新商品", description = "后台更新商品信息")
    public Result<Void> updateProduct(@RequestBody AdminProductDTO adminProductDTO) {
        adminService.updateProduct(adminProductDTO);
        return Result.success();
    }

    @DeleteMapping("/product/{id}")
    @Operation(summary = "删除商品", description = "后台删除商品")
    public Result<Void> deleteProduct(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        adminService.deleteProduct(id);
        return Result.success();
    }

    // ========== Order Management ==========
    @GetMapping("/order/page")
    @Operation(summary = "订单分页列表", description = "后台分页查询订单")
    public Result<PageVO<OrderVO>> getOrderPage(AdminOrderDTO adminOrderDTO) {
        return Result.success(adminService.getOrderPage(adminOrderDTO));
    }

    @PatchMapping("/order/{id}/status")
    @Operation(summary = "修改订单状态", description = "后台修改订单状态")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "订单状态") @RequestParam Integer status) {
        adminService.updateOrderStatus(id, status);
        return Result.success();
    }
}
