package com.example.recover;

import com.example.model.Order;
import com.example.service.RemoteServiceException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 支付回退回调类
 *
 * 什么是回退（Recover）机制？
 * 当重试次数用尽后仍然失败时，系统不会简单地抛出一个异常让用户困惑，
 * 而是会调用预先定义好的回退方法，进行优雅的降级处理。
 *
 * 回退场景举例：
 * 1. 第三方支付接口连续失败 → 回退：记录日志，标记订单为"待人工处理"
 * 2. 数据库连接超时 → 回退：写入消息队列，稍后重试
 * 3. 远程服务不可用 → 回退：返回缓存数据或默认结果
 *
 * 本类演示了不同异常类型对应的回退方法实现
 */
@Component
public class PaymentRecoverCallback {

    /**
     * 回退方法：处理 RemoteServiceException 异常
     *
     * 什么时候会触发这个回退？
     * 当远程服务调用（如HTTP请求、RMI调用）失败，且重试3次后仍然失败时调用。
     * 常见原因：网络断开、服务器宕机、防火墙阻止等
     *
     * @param e 捕获到的 RemoteServiceException 异常
     * @param order 涉及的订单对象
     * @return 回退处理后的结果（一个标记为待人工处理的订单）
     */
    public Order recoverFromRemoteServiceException(RemoteServiceException e, Order order) {
        // 记录错误日志，便于排查问题
        // 生产环境中应该使用专业的日志框架（如Logback）并记录到日志聚合系统
        System.out.println("【回退处理】远程服务调用失败，已达到最大重试次数");
        System.out.println("【回退处理】订单ID：" + order.getOrderId());
        System.out.println("【回退处理】错误信息：" + e.getMessage());
        System.out.println("【回退处理】建议：检查网络连接或远程服务状态");

        // 回退策略：将订单标记为待人工处理状态
        // 实际生产中，可能还会：
        // 1. 发送告警通知给运维人员
        // 2. 将订单信息写入待处理队列
        // 3. 记录到数据库的异常表中
        order.setStatus(3); // 支付失败状态
        order.setDescription("支付失败 - 待人工处理（远程服务不可用）");

        return order;
    }

    /**
     * 回退方法：处理 IllegalStateException 异常
     *
     * 什么时候会触发这个回退？
     * 当业务逻辑状态不符合预期时触发。
     * 例如：支付渠道返回"订单已过期"或"余额不足"等业务错误。
     *
     * @param e 捕获到的 IllegalStateException 异常
     * @param order 涉及的订单对象
     * @return 回退处理后的结果
     */
    public Order recoverFromIllegalStateException(IllegalStateException e, Order order) {
        System.out.println("【回退处理】业务状态异常，已达到最大重试次数");
        System.out.println("【回退处理】订单ID：" + order.getOrderId());
        System.out.println("【回退处理】业务错误：" + e.getMessage());

        // 回退策略：根据不同的业务错误返回不同的处理结果
        // 这里简单标记为失败，实际可以根据异常信息做更精细的处理
        order.setStatus(3);
        order.setDescription("支付失败 - 业务状态异常：" + e.getMessage());

        return order;
    }

    /**
     * 回退方法：处理运行时异常（通用回退）
     *
     * 这是最后一个回退方法，用于处理没有被特定方法捕获的异常
     * 通过异常类型匹配规则，Spring Retry 会自动选择最匹配的回退方法
     *
     * @param e 捕获到的 RuntimeException 异常
     * @param order 涉及的订单对象
     * @return 回退处理后的结果
     */
    public Order recoverFromRuntimeException(RuntimeException e, Order order) {
        System.out.println("【回退处理】运行时异常，已达到最大重试次数");
        System.out.println("【回退处理】订单ID：" + order.getOrderId());
        System.out.println("【回退处理】异常类型：" + e.getClass().getName());

        order.setStatus(3);
        order.setDescription("支付失败 - 系统异常");

        return order;
    }

    /**
     * 回退方法：处理 ArithmeticException（算术异常）
     *
     * 演示如何处理更具体的异常类型
     * 例如：当金额计算出现异常时（如除以零）
     *
     * @param e 捕获到的 ArithmeticException 异常
     * @param amount 原始金额参数
     * @return 回退处理结果
     */
    public BigDecimal recoverFromArithmeticException(ArithmeticException e, BigDecimal amount) {
        System.out.println("【回退处理】算术异常");
        System.out.println("【回退处理】原始金额：" + amount);
        System.out.println("【回退处理】错误信息：" + e.getMessage());

        // 回退策略：返回0或一个安全的默认值
        return BigDecimal.ZERO;
    }
}
