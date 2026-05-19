package com.example.admin.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private Long id;
    private String code;
    private String name;
    private String path;
    private String component;
    private Integer type;
    private Long parentId;
    private Integer sort;
    private String icon;
    private Integer status;
}
