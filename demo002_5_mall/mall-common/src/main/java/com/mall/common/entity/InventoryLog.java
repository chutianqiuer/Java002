package com.mall.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Inventory log entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory_logs")
public class InventoryLog extends BaseEntity {

    private Long productId;

    private String orderNo;

    private Integer changeType;

    private Integer beforeStock;

    private Integer afterStock;

    private Integer changeQuantity;

    private String operator;
}
