package com.example.web.controller;

import com.example.common.dto.PageDTO;
import com.example.common.dto.ProductDTO;
import com.example.common.vo.PageVO;
import com.example.common.vo.ProductVO;
import com.example.common.vo.Result;
import com.example.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody ProductDTO productDTO) {
        return Result.success(productService.create(productDTO));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getById(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @GetMapping("/page")
    public Result<PageVO<ProductVO>> getPage(ProductDTO productDTO) {
        return Result.success(productService.getPage(productDTO));
    }

    @GetMapping("/recommend")
    public Result<List<ProductVO>> getRecommend() {
        return Result.success(productService.getRecommendProducts());
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody ProductDTO productDTO) {
        productService.update(productDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }
}
