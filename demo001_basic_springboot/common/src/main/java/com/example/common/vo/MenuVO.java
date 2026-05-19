package com.example.common.vo;

import lombok.Data;

@Data
public class MenuVO {
    private Long id;
    private String name;
    private String path;
    private String component;
    private Integer type;
    private Long parentId;
    private String icon;
    private Integer sort;
    private Integer status;
}
