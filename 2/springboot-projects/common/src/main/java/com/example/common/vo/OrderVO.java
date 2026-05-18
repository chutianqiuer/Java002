package com.example.common.vo;

import com.example.common.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO extends BaseVO {
    private String orderNo;
    private Long userId;
    private String userName;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private OrderStatus status;
    private String statusDesc;
    private LocalDateTime orderTime;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private String remark;
    private List<OrderItemVO> items;
}
