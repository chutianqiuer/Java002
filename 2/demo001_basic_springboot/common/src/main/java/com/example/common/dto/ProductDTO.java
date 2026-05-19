package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDTO extends PageDTO {
    @NotBlank(message = "产品名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    private String image;
    private String images;
    private Long categoryId;
    private Integer status;
}
