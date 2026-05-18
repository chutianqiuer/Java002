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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // User Management
    @GetMapping("/user/page")
    public Result<PageVO<UserVO>> getUserPage(AdminUserDTO adminUserDTO) {
        return Result.success(adminService.getUserPage(adminUserDTO));
    }

    @PostMapping("/user")
    public Result<Void> createUser(@RequestBody AdminUserDTO adminUserDTO) {
        adminService.createUser(adminUserDTO);
        return Result.success();
    }

    @PutMapping("/user")
    public Result<Void> updateUser(@RequestBody AdminUserDTO adminUserDTO) {
        adminService.updateUser(adminUserDTO);
        return Result.success();
    }

    @DeleteMapping("/user/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success();
    }

    // Product Management
    @GetMapping("/product/page")
    public Result<PageVO<ProductVO>> getProductPage(AdminProductDTO adminProductDTO) {
        return Result.success(adminService.getProductPage(adminProductDTO));
    }

    @PostMapping("/product")
    public Result<Long> createProduct(@RequestBody AdminProductDTO adminProductDTO) {
        return Result.success(adminService.createProduct(adminProductDTO));
    }

    @PutMapping("/product")
    public Result<Void> updateProduct(@RequestBody AdminProductDTO adminProductDTO) {
        adminService.updateProduct(adminProductDTO);
        return Result.success();
    }

    @DeleteMapping("/product/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return Result.success();
    }

    // Order Management
    @GetMapping("/order/page")
    public Result<PageVO<OrderVO>> getOrderPage(AdminOrderDTO adminOrderDTO) {
        return Result.success(adminService.getOrderPage(adminOrderDTO));
    }

    @PatchMapping("/order/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateOrderStatus(id, status);
        return Result.success();
    }
}
