package com.example.service;

import com.example.model.Order;
import org.springframework.stereotype.Service;

/**
 * 支付服务类 - 演示 Spring Retry 的重试机制
 *
 * 为什么支付服务需要重试机制？
 * 在真实的支付场景中，以下情况非常常见：
 * 1. 网络抖动：用户的网络或银行的网络出现短暂的抖动
 * 2. 服务器繁忙：银行接口处理能力有限，高峰期响应慢
 * 3. 超时设置：第一次请求可能因为超时而失败，但实际银行已扣款
 * 4. 幂等性：支付接口通常设计为幂等的，多次调用不会重复扣款
 *
 * 通过重试机制，我们可以：
 * - 提高支付成功率
 * - 避免因临时故障导致用户支付失败
 * - 减少客诉和人工处理成本
 */
@Service
public class PaymentService {

    /**
     * 模拟的计数器，用于追踪方法调用次数
     * 正常情况下只应调用1次，设置失败标志后需要多次重试
     */
    private int callCount = 0;

    /**
     * 标记是否模拟失败，用于测试重试机制
     */
    private boolean simulateFailure = true;

    /**
     * 支付方法 - 使用 @Retryable 注解实现自动重试
     *
     * @Retryable 注解参数详解：
     *
     * 1. value（Class[]）：指定需要重试的异常类型
     *    - 只有抛出这些异常时才触发重试
     *    - 如果不指定，默认重试所有 RuntimeException
     *
     * 2. maxAttempts（int）：最大重试次数
     *    - 包括第一次调用，所以 maxAttempts=3 意味着最多调用4次
     *    - 默认值是3
     *
     * 3. backoff（Backoff）：退避策略配置
     *    - @Backoff 注解用于配置重试间隔
     *    - delay：初始延迟时间（毫秒）
     *    - multiplier：倍数因子，用于指数退避
     *    - maxDelay：最大延迟时间（毫秒）
     *
     * 【重试流程示例】
     * 假设调用失败，maxAttempts=3, delay=1000, multiplier=2：
     * - 第1次尝试：立即发起
     * - 第1次失败：等待 1000ms
     * - 第2次尝试：发起第二次调用
     * - 第2次失败：等待 2000ms（1000 * 2）
     * - 第3次尝试：发起第三次调用
     * - 第3次失败：不再重试，调用回退方法
     *
     * @param order 订单对象
     * @return 支付结果描述
     */
    @org.springframework.retry.annotation.Retryable(
        value = RemoteServiceException.class,  // 只重试 RemoteServiceException 异常
        maxAttempts = 3,                        // 最大重试3次（加上原始调用共4次）
        backoff = @org.springframework.retry.annotation.Backoff(
            delay = 1000,                      // 初始延迟1秒
            multiplier = 2.0                    // 指数退避倍数
        )
    )
    public String processPayment(Order order) {
        callCount++;
        System.out.println("【PaymentService】第 " + callCount + " 次尝试调用支付接口...");
        System.out.println("【PaymentService】订单ID：" + order.getOrderId() + "，金额：" + order.getAmount());

        // 模拟远程服务调用
        // 如果 simulateFailure 为 true，前3次调用会抛出异常
        if (simulateFailure && callCount < 4) {
            System.out.println("【PaymentService】模拟网络异常！");
            throw new RemoteServiceException("网络连接超时，远程服务不可用");
        }

        // 模拟成功
        System.out.println("【PaymentService】支付接口调用成功！");
        return "支付成功，订单号：" + order.getOrderId();
    }

    /**
     * 重置计数器
     * 用于测试时控制重试行为
     */
    public void reset() {
        callCount = 0;
    }

    /**
     * 设置是否模拟失败
     */
    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}

/**
 * 远程服务调用异常
 * 用于模拟远程服务调用时可能出现的网络故障、超时等问题
 *
 * 在实际项目中，远程服务调用通常会遇到：
 * - 网络中断或抖动
 * - 服务端处理超时
 * - 连接被拒绝
 * - 服务端暂时不可用
 *
 * 这类异常通常是临时性的，非常适合通过重试机制来处理
 */
class RemoteServiceException extends RuntimeException {
    public RemoteServiceException(String message) {
        super(message);
    }

    public RemoteServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
