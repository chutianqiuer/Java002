package com.mall.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Operation log entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("operation_logs")
public class OperationLog extends BaseEntity {

    private Long userId;

    private String username;

    private String module;

    private String operation;

    private String method;

    private String params;

    private String result;

    private Integer status;

    private String ip;

    private Long duration;
}
