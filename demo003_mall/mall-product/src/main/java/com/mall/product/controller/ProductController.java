package com.mall.product.controller;

import com.mall.common.entity.Product;
import com.mall.common.response.ApiResponse;
import com.mall.common.response.PageResponse;
import com.mall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<Product> createProduct(@Valid @RequestBody Product product) {
        return ApiResponse.success(productService.createProduct(product));
    }

    @PutMapping
    public ApiResponse<Product> updateProduct(@Valid @RequestBody Product product) {
        return ApiResponse.success(productService.updateProduct(product));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getProduct(@PathVariable Long id) {
        return ApiResponse.success(productService.getById(id));
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Product>> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResponse.of(
                productService.countProducts(),
                page,
                size,
                productService.listProducts(page, size)
        ));
    }

    @GetMapping("/search")
    public ApiResponse<List<Product>> searchByName(@RequestParam String name) {
        return ApiResponse.success(productService.searchByName(name));
    }

    @PostMapping("/deduct-stock")
    public ApiResponse<Boolean> deductStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String orderNo) {
        return ApiResponse.success(productService.deductStock(productId, quantity, orderNo));
    }

    @PostMapping("/restore-stock")
    public ApiResponse<Boolean> restoreStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String orderNo) {
        return ApiResponse.success(productService.restoreStock(productId, quantity, orderNo));
    }
}
