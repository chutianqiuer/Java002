package com.example.controller;

import com.example.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 *
 * 本控制器演示 @RestController 的使用，这是Spring Boot中最常用的控制器类型。
 *
 * @RestController = @Controller + @ResponseBody
 *
 * 特点：
 * - 所有方法的返回值都会直接写入HTTP响应体
 * - 不再适合返回JSP视图
 * - 主要用于开发RESTful风格的API
 *
 * 本控制器提供完整的CRUD操作，演示：
 * 1. RESTful风格的URL设计
 * 2. @PathVariable - URL路径变量
 * 3. @RequestBody - JSON请求体
 * 4. ResponseEntity - 完整的HTTP响应控制
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    /**
     * 模拟商品数据存储
     * 实际项目中应该使用数据库
     */
    private static final Map<Long, Product> productMap = new HashMap<>();

    // 静态初始化测试数据
    static {
        productMap.put(1L, new Product(1L, "iPhone 14", "苹果手机", new BigDecimal("6999.00"), 100, "电子产品"));
        productMap.put(2L, new Product(2L, "MacBook Pro", "苹果笔记本电脑", new BigDecimal("12999.00"), 50, "电子产品"));
        productMap.put(3L, new Product(3L, "AirPods Pro", "苹果无线耳机", new BigDecimal("1899.00"), 200, "配件"));
    }

    // ==================== 查询操作 ====================

    /**
     * 获取所有商品
     *
     * GET /products
     *
     * @return 所有商品的列表（JSON数组）
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    /**
     * 根据ID获取商品
     *
     * GET /products/{id}
     * 示例：GET /products/1
     *
     * @param id 商品ID（从URL路径获取）
     * @return 商品信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productMap.get(id);

        if (product == null) {
            // 返回404 Not Found
            return ResponseEntity.notFound().build();
        }

        // 返回200 OK和商品数据
        return ResponseEntity.ok(product);
    }

    /**
     * 根据分类获取商品
     *
     * GET /products?category=电子产品
     * 或
     * GET /products/category/电子产品
     *
     * 两种URL设计都可以实现相同的功能
     *
     * @param category 商品分类（从请求参数获取）
     * @return 该分类下的所有商品
     */
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        List<Product> result = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getCategory().equals(category)) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * 搜索商品（按名称模糊搜索）
     *
     * GET /products/search?name=iPhone
     *
     * @param name 商品名称关键字
     * @return 匹配的商品列表
     */
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        List<Product> result = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getName().contains(name)) {
                result.add(product);
            }
        }
        return result;
    }

    // ==================== 创建操作 ====================

    /**
     * 创建新商品
     *
     * POST /products
     * Content-Type: application/json
     * Body: {"name": "商品名称", "price": 99.99, ...}
     *
     * @RequestBody 注解：
     * - 将HTTP请求体中的JSON数据绑定到Product对象
     * - 需要消息转换器（Spring MVC已配置Jackson）
     *
     * @param product 商品对象（从请求体JSON转换）
     * @return 创建的商品（带ID）
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // 模拟ID生成（实际由数据库生成）
        Long newId = System.currentTimeMillis();
        product.setId(newId);

        // 保存到存储
        productMap.put(newId, product);

        // 返回201 Created和商品数据
        // 同时在Location响应头中包含新商品的URL
        return ResponseEntity
                .status(201)
                .header("Location", "/products/" + newId)
                .body(product);
    }

    // ==================== 更新操作 ====================

    /**
     * 更新商品（完全更新）
     *
     * PUT /products/{id}
     * Content-Type: application/json
     * Body: {"name": "新名称", "price": 99.99, ...}
     *
     * PUT要求提交完整的资源数据，
     * 如果只提交部分字段，未提交的字段会被置为null或默认值
     *
     * @param id      商品ID（路径变量）
     * @param product 新商品数据（请求体）
     * @return 更新后的商品
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        // 检查商品是否存在
        if (!productMap.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        // 确保ID不变
        product.setId(id);

        // 更新数据
        productMap.put(id, product);

        return ResponseEntity.ok(product);
    }

    /**
     * 部分更新商品
     *
     * PATCH /products/{id}
     * Content-Type: application/json
     * Body: {"price": 89.99}
     *
     * PATCH与PUT的区别：
     * - PUT：完全更新，必须提交所有字段
     * - PATCH：部分更新，只提交需要修改的字段
     *
     * @param id      商品ID
     * @param product 部分数据
     * @return 更新后的商品
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        // 获取现有商品
        Product existing = productMap.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // 只更新非null的字段（简化处理）
        if (product.getName() != null) {
            existing.setName(product.getName());
        }
        if (product.getDescription() != null) {
            existing.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            existing.setPrice(product.getPrice());
        }
        if (product.getStock() != null) {
            existing.setStock(product.getStock());
        }
        if (product.getCategory() != null) {
            existing.setCategory(product.getCategory());
        }

        // 保存更新
        productMap.put(id, existing);

        return ResponseEntity.ok(existing);
    }

    // ==================== 删除操作 ====================

    /**
     * 删除商品
     *
     * DELETE /products/{id}
     *
     * @param id 商品ID
     * @return 204 No Content（成功删除，无返回内容）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // 检查商品是否存在
        if (!productMap.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        // 删除
        productMap.remove(id);

        // 返回204 No Content
        return ResponseEntity.noContent().build();
    }

    // ==================== 其他操作 ====================

    /**
     * 获取商品总数
     *
     * GET /products/count
     *
     * @return 商品总数
     */
    @GetMapping("/count")
    public Map<String, Object> getProductCount() {
        Map<String, Object> result = new HashMap<>();
        result.put("count", productMap.size());
        result.put("products", productMap.values());
        return result;
    }

    /**
     * 减少库存
     *
     * POST /products/{id}/reduce-stock
     *
     * 这是一个业务操作端点，不属于标准的RESTful设计
     * 但在实际项目中很常见
     *
     * @param id       商品ID
     * @param quantity 减少数量
     * @return 更新后的商品
     */
    @PostMapping("/{id}/reduce-stock")
    public ResponseEntity<Product> reduceStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        Product product = productMap.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        if (product.getStock() < quantity) {
            return ResponseEntity.badRequest().build();
        }

        product.setStock(product.getStock() - quantity);
        productMap.put(id, product);

        return ResponseEntity.ok(product);
    }
}
