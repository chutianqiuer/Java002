package com.example.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prd_category")
public class Category extends BaseEntity {
    private String name;
    private Long parentId;
    private Integer sort;
    private String icon;
    private Integer status;
}
