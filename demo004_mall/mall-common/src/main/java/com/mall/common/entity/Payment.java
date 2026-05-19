package com.mall.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Payment entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payments")
public class Payment extends BaseEntity {

    private String paymentNo;

    private String orderNo;

    private Long userId;

    private BigDecimal amount;

    private Integer paymentMethod;

    private Integer status;

    private String transactionId;

    private String paidTime;
}
