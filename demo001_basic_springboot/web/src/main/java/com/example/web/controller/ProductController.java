package com.example.web.controller;

import com.example.common.dto.ProductDTO;
import com.example.common.vo.PageVO;
import com.example.common.vo.ProductVO;
import com.example.common.vo.Result;
import com.example.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Tag(name = "商品管理", description = "商品CRUD、搜索、推荐")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "创建商品", description = "添加新商品")
    public Result<Long> create(@Valid @RequestBody ProductDTO productDTO) {
        return Result.success(productService.create(productDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品信息")
    public Result<ProductVO> getById(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "商品分页列表", description = "分页查询商品，支持按名称搜索")
    public Result<PageVO<ProductVO>> getPage(ProductDTO productDTO) {
        return Result.success(productService.getPage(productDTO));
    }

    @GetMapping("/recommend")
    @Operation(summary = "推荐商品", description = "获取推荐商品列表")
    public Result<List<ProductVO>> getRecommend() {
        return Result.success(productService.getRecommendProducts());
    }

    @PutMapping
    @Operation(summary = "更新商品", description = "修改商品信息")
    public Result<Void> update(@Valid @RequestBody ProductDTO productDTO) {
        productService.update(productDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "根据ID删除商品")
    public Result<Void> delete(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }
}
