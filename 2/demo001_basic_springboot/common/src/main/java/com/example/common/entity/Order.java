package com.example.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.enums.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ord_order")
public class Order extends BaseEntity {
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private String remark;
}
