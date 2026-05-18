package com.example.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品实体类
 *
 * 本类用于演示Spring MVC中RESTful风格的URL参数绑定（@PathVariable），
 * 以及复杂对象的JSON序列化和反序列化。
 *
 * RESTful风格示例：
 * - GET /products/1     获取ID为1的商品
 * - GET /products/list  获取商品列表
 * - POST /products      创建商品
 * - PUT /products/1    更新ID为1的商品
 * - DELETE /products/1  删除ID为1的商品
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格（使用BigDecimal避免浮点数精度问题）
     */
    private BigDecimal price;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 默认构造函数
     */
    public Product() {
    }

    /**
     * 带参构造函数
     * @param id 商品ID
     * @param name 商品名称
     * @param price 商品价格
     */
    public Product(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * 全参构造函数
     */
    public Product(Long id, String name, String description, BigDecimal price, Integer stock, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // ==================== Getter和Setter方法 ====================

    /**
     * 获取商品ID
     * @return 商品ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置商品ID
     * @param id 商品ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取商品名称
     * @return 商品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置商品名称
     * @param name 商品名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取商品描述
     * @return 商品描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置商品描述
     * @param description 商品描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取商品价格
     * @return 商品价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置商品价格
     * @param price 商品价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取库存数量
     * @return 库存数量
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * 设置库存数量
     * @param stock 库存数量
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * 获取商品分类
     * @return 商品分类
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置商品分类
     * @param category 商品分类
     */
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                '}';
    }
}
