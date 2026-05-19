package com.mall.common.event;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Order created event for RocketMQ
 */
@Data
public class OrderCreatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private BigDecimal totalAmount;

    private String createTime;
}
