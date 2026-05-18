package com.example.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageDTO extends BaseDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
}
