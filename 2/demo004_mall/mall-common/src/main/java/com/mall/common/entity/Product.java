package com.mall.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Product entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("products")
public class Product extends BaseEntity {

    private String productName;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String unit;

    private String category;

    private String imageUrl;

    private Integer status;
}
