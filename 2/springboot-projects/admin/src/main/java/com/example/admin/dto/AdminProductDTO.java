package com.example.admin.dto;

import com.example.common.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminProductDTO extends PageDTO {
    private String name;
    private Long categoryId;
    private Integer status;
}
