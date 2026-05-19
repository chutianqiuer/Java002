package com.mall.common.constants;

/**
 * Payment status constants
 */
public class PaymentStatus {

    public static final int PENDING = 0;           // 待支付
    public static final int SUCCESS = 1;            // 支付成功
    public static final int FAILED = 2;             // 支付失败
    public static final int REFUNDING = 3;          // 退款中
    public static final int REFUNDED = 4;           // 已退款
}
