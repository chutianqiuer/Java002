package com.example.common.enums;

public enum PaymentStatus {
    UNPAID("未支付"),
    PAID("已支付"),
    REFUNDING("退款中"),
    REFUNDED("已退款"),
    FAILED("支付失败");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
