package com.example.service;

import com.example.model.Order;
import com.example.recover.PaymentRecoverCallback;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 订单服务类 - 演示 Spring Retry 的多种重试策略
 *
 * 本类展示实际项目中可能遇到的多种重试场景：
 * 1. 固定延迟重试：适用于对响应时间敏感的场景
 * 2. 指数退避重试：适用于远程API调用，避免对服务造成压力
 * 3. 指定异常类型重试：只对特定异常进行重试
 * 4. 带回退方法的retry：重试耗尽后执行降级处理
 */
@Service
public class OrderService {

    private final PaymentService paymentService;

    /**
     * 构造器注入 PaymentService
     * Spring Retry 的 @Retryable 注解只能用在 Spring Bean 的方法上
     * 因此我们让 OrderService 调用 PaymentService 来演示重试
     */
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 计数器：追踪各方法的调用次数
     */
    private int fixedDelayCallCount = 0;
    private int exponentialCallCount = 0;
    private int multiExceptionCallCount = 0;
    private int withRecoverCallCount = 0;

    /**
     * 创建订单并演示固定延迟重试策略
     *
     * 什么是固定延迟策略（FixedBackOffPolicy）？
     * 每次重试之间的等待时间固定不变。
     * 例如：delay=2000 表示每次失败后都等待2秒再重试。
     *
     * 适用场景：
     * - 对延迟有明确要求的接口
     * - 重试次数较少的场景
     * - 简单的网络调用
     *
     * 优点：简单、可预测
     * 缺点：没有"试探"效果，可能会给服务造成压力
     *
     * @param amount 订单金额
     * @return 创建的订单对象
     */
    @org.springframework.retry.annotation.Retryable(
        value = Exception.class,              // 捕获所有 Exception 类型的异常
        maxAttempts = 3,                       // 最多重试3次
        backoff = @org.springframework.retry.annotation.Backoff(
            delay = 2000                       // 固定延迟2秒
        )
    )
    public Order createOrderWithFixedDelay(BigDecimal amount) {
        fixedDelayCallCount++;
        System.out.println("\n【固定延迟策略】第 " + fixedDelayCallCount + " 次尝试...");
        System.out.println("【固定延迟策略】当前延迟配置：固定 2000ms");

        // 模拟业务逻辑
        // 假设这里调用了一个不稳定的第三方服务
        if (fixedDelayCallCount < 3) {
            throw new RuntimeException("第三方服务暂时不可用");
        }

        // 模拟成功创建订单
        Order order = new Order();
        order.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8));
        order.setAmount(amount);
        order.setStatus(2); // 支付成功
        order.setCreateTime(LocalDateTime.now());
        order.setPayTime(LocalDateTime.now());
        order.setDescription("订单创建成功");

        System.out.println("【固定延迟策略】订单创建成功！订单ID：" + order.getOrderId());
        return order;
    }

    /**
     * 创建订单并演示指数退避重试策略
     *
     * 什么是指数退避策略（ExponentialBackOffPolicy）？
     * 每次重试的等待时间成指数增长。
     * 例如：delay=1000, multiplier=2 表示等待时间依次为 1s, 2s, 4s, 8s...
     *
     * 适用场景：
     * - 远程API调用
     * - 需要避免对下游服务造成压力的场景
     * - 重试次数较多的场景
     *
     * 优点：
     * - 减少对服务的压力
     * - 给服务恢复更多时间
     * - "试探性"地等待服务恢复
     *
     * 缺点：初始失败时响应较慢
     *
     * @param amount 订单金额
     * @return 创建的订单对象
     */
    @org.springframework.retry.annotation.Retryable(
        value = RemoteServiceException.class,  // 只针对 RemoteServiceException 重试
        maxAttempts = 4,                       // 最多重试4次
        backoff = @org.springframework.retry.annotation.Backoff(
            delay = 1000,                      // 初始延迟1秒
            multiplier = 2.0,                  // 每次延迟翻倍
            maxDelay = 10000                   // 最大延迟10秒（避免等待太久）
        )
    )
    public Order createOrderWithExponentialBackoff(BigDecimal amount) {
        exponentialCallCount++;
        System.out.println("\n【指数退避策略】第 " + exponentialCallCount + " 次尝试...");
        System.out.println("【指数退避策略】初始延迟 1000ms，倍数 2.0，最大延迟 10000ms");

        // 模拟不稳定的第三方服务
        if (exponentialCallCount < 4) {
            System.out.println("【指数退避策略】服务调用失败，触发指数退避...");
            throw new RemoteServiceException("网络连接不稳定");
        }

        // 成功
        Order order = new Order();
        order.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8));
        order.setAmount(amount);
        order.setStatus(2);
        order.setCreateTime(LocalDateTime.now());
        order.setPayTime(LocalDateTime.now());
        order.setDescription("指数退避重试成功");

        System.out.println("【指数退避策略】订单创建成功！");
        return order;
    }

    /**
     * 演示多异常类型重试
     *
     * retryFor 属性：指定需要重试的异常类型
     * 如果不指定，默认重试所有 RuntimeException
     *
     * 我们可以针对不同的异常类型配置不同的重试策略
     */
    @org.springframework.retry.annotation.Retryable(
        value = {RemoteServiceException.class, IllegalStateException.class},
        maxAttempts = 3,
        backoff = @org.springframework.retry.annotation.Backoff(delay = 1500)
    )
    public Order createOrderWithMultiException(BigDecimal amount) {
        multiExceptionCallCount++;
        System.out.println("\n【多异常类型重试】第 " + multiExceptionCallCount + " 次尝试...");

        if (multiExceptionCallCount == 1) {
            // 第一次：模拟网络异常
            throw new RemoteServiceException("网络连接失败");
        } else if (multiExceptionCallCount == 2) {
            // 第二次：模拟业务状态异常
            throw new IllegalStateException("订单状态已过期");
        }

        // 第三次成功
        Order order = new Order();
        order.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8));
        order.setAmount(amount);
        order.setStatus(2);
        order.setCreateTime(LocalDateTime.now());
        order.setPayTime(LocalDateTime.now());
        order.setDescription("多异常类型重试成功");

        return order;
    }

    /**
     * 演示带回退（Recover）机制的重试
     *
     * recover 属性：指定重试失败后的回调方法
     * 当所有重试都失败后，Spring Retry 会自动调用指定的回退方法
     *
     * 回退方法的要求：
     * 1. 返回类型必须与原始方法一致（或兼容）
     * 2. 参数列表的第一个参数必须是异常类型
     * 3. 后续参数必须与原始方法参数一致
     * 4. 需要使用 @Recover 注解
     *
     * 【完整流程】
     * 1. 调用方法 → 抛出异常
     * 2. 满足重试条件 → 开始重试
     * 3. 重试 N 次后仍然失败
     * 4. 调用回退（Recovery）方法
     * 5. 返回降级处理的结果
     *
     * @param order 订单对象
     * @return 处理结果
     */
    @org.springframework.retry.annotation.Retryable(
        value = {RemoteServiceException.class, IllegalStateException.class},
        maxAttempts = 3,
        backoff = @org.springframework.retry.annotation.Backoff(delay = 1000),
        recover = "paymentRecoverCallback.recoverFromRemoteServiceException"
    )
    public String processPaymentWithRecover(Order order) {
        withRecoverCallCount++;
        System.out.println("\n【带回退的重试】第 " + withRecoverCallCount + " 次尝试...");
        System.out.println("【带回退的重试】订单ID：" + order.getOrderId());

        if (withRecoverCallCount < 3) {
            throw new RemoteServiceException("远程支付服务暂时不可用");
        }

        return "支付成功";
    }

    /**
     * 打印重试统计信息
     */
    public void printStatistics() {
        System.out.println("\n========== 重试统计 ==========");
        System.out.println("固定延迟策略调用次数：" + fixedDelayCallCount);
        System.out.println("指数退避策略调用次数：" + exponentialCallCount);
        System.out.println("多异常类型重试调用次数：" + multiExceptionCallCount);
        System.out.println("带回退的重试调用次数：" + withRecoverCallCount);
        System.out.println("==============================\n");
    }

    /**
     * 重置所有计数器（用于测试）
     */
    public void resetAllCounters() {
        fixedDelayCallCount = 0;
        exponentialCallCount = 0;
        multiExceptionCallCount = 0;
        withRecoverCallCount = 0;
    }
}
