package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 用于演示重试机制在实际业务场景中的应用
 *
 * 为什么需要订单类？
 * 在实际的支付场景中，订单创建后可能需要调用第三方支付接口，
 * 由于网络波动、第三方服务繁忙等原因，第一次调用可能失败，
 * 这时候就需要重试机制来保证交易的可靠性。
 */
public class Order {

    /**
     * 订单ID - 订单的唯一标识符
     * 格式：类似于 "ORD20260305001" 的字符串
     */
    private String orderId;

    /**
     * 订单金额 - 用户需要支付的金额
     * 使用 BigDecimal 类型避免浮点数精度问题
     * 例如：99.99 表示 99元9角9分
     */
    private BigDecimal amount;

    /**
     * 订单状态 - 标识订单的当前处理阶段
     * 0 = 待支付
     * 1 = 支付中
     * 2 = 支付成功
     * 3 = 支付失败
     */
    private Integer status;

    /**
     * 订单创建时间 - 记录订单创建的时间戳
     * 用于追踪订单的生命周期
     */
    private LocalDateTime createTime;

    /**
     * 支付完成时间 - 记录支付成功的时间戳
     * 如果支付失败，则为 null
     */
    private LocalDateTime payTime;

    /**
     * 订单描述 - 订单的商品或服务描述
     * 例如："购买会员服务"、"订单编号：xxx"
     */
    private String description;

    /**
     * 无参构造函数
     */
    public Order() {
    }

    /**
     * 全参构造函数
     */
    public Order(String orderId, BigDecimal amount, Integer status,
                 LocalDateTime createTime, LocalDateTime payTime, String description) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.createTime = createTime;
        this.payTime = payTime;
        this.description = description;
    }

    // Getter 和 Setter 方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", createTime=" + createTime +
                ", payTime=" + payTime +
                ", description='" + description + '\'' +
                '}';
    }
}
