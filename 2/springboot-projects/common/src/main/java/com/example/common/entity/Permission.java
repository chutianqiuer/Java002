package com.example.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {
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
