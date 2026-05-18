package com.example.common.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductVO extends BaseVO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private String images;
    private Long categoryId;
    private String categoryName;
    private Integer status;
}
