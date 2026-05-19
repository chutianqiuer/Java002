package com.mall.common.constants;

/**
 * Order status constants
 */
public class OrderStatus {

    public static final int PENDING = 0;           // 待支付
    public static final int PAID = 1;              // 已支付
    public static final int SHIPPED = 2;           // 已发货
    public static final int CONFIRMED = 3;         // 已确认收货
    public static final int CANCELLED = 4;         // 已取消
    public static final int REFUNDING = 5;         // 退款中
    public static final int REFUNDED = 6;           // 已退款
}
