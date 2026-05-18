package com.example;

import com.example.model.Order;
import com.example.service.OrderService;
import com.example.service.PaymentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

/**
 * Spring Retry 演示项目启动类
 *
 * 本项目演示 Spring Retry 重试机制的核心功能：
 *
 * 1. @EnableRetry - 启用 Spring Retry 功能
 *    在配置类或启动类上添加 @EnableRetry 注解，即可开启基于注解的重试支持
 *
 * 2. @Retryable - 标注需要重试的方法
 *    - value: 指定重试的异常类型
 *    - maxAttempts: 最大重试次数
 *    - backoff: 退避策略配置
 *
 * 3. @Backoff - 配置退避策略
 *    - delay: 初始延迟时间
 *    - multiplier: 指数倍数
 *    - maxDelay: 最大延迟时间
 *
 * 4. @Recover - 定义回退（降级）方法
 *    当重试次数用尽后仍然失败时，自动调用回退方法
 *
 * 为什么需要重试机制？
 * 在分布式系统中，网络波动、服务抖动、临时故障是常态。
 * 重试机制可以让系统在遇到临时性故障时自动恢复，提高系统的可用性。
 *
 * 典型的临时性故障场景：
 * 1. 网络抖动导致的请求超时
 * 2. 数据库连接池耗尽（瞬时）
 * 3. 第三方服务暂时不可用
 * 4. 服务器JVM GC导致的停顿
 * 5. 负载均衡导致的请求分发到不健康的节点
 */
@SpringBootApplication
public class Demo010Application {

    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(Demo010Application.class, args);

        // 等待 Spring 容器启动完成后执行演示
        System.out.println("\n========================================");
        System.out.println("   Spring Retry 重试机制演示项目启动   ");
        System.out.println("========================================\n");

        // 稍作延迟，确保所有 Bean 都初始化完成
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 获取服务 Bean
        Demo010Application app = new Demo010Application();
        // 注意：实际项目中应该通过构造器注入获取 Bean
        // 这里为了演示方便，使用了 ApplicationContext
        org.springframework.context.ApplicationContext context =
            org.springframework.boot.SpringApplication.run(Demo010Application.class, args);

        PaymentService paymentService = context.getBean(PaymentService.class);
        OrderService orderService = context.getBean(OrderService.class);

        // 执行演示
        app.runDemos(paymentService, orderService);
    }

    /**
     * 运行各种重试场景演示
     */
    private void runDemos(PaymentService paymentService, OrderService orderService) {
        try {
            // ========== 演示1：基本重试机制 ==========
            System.out.println("\n========================================");
            System.out.println("演示1：Spring Retry 基本重试机制");
            System.out.println("========================================");
            demonstrateBasicRetry(paymentService);

            // ========== 演示2：固定延迟重试 ==========
            System.out.println("\n========================================");
            System.out.println("演示2：固定延迟重试策略");
            System.out.println("========================================");
            demonstrateFixedDelayRetry(orderService);

            // ========== 演示3：指数退避重试 ==========
            System.out.println("\n========================================");
            System.out.println("演示3：指数退避重试策略");
            System.out.println("========================================");
            demonstrateExponentialBackoff(orderService);

            // ========== 演示4：带回退的重试 ==========
            System.out.println("\n========================================");
            System.out.println("演示4：带回退机制的重试");
            System.out.println("========================================");
            demonstrateRetryWithRecover(orderService);

            // 打印统计信息
            orderService.printStatistics();

            System.out.println("\n========================================");
            System.out.println("        所有演示执行完成！");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("演示执行过程中发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 演示1：基本重试机制
     *
     * 演示 PaymentService.processPayment() 方法的重试机制
     * 该方法使用 @Retryable 注解，配置了：
     * - 最大重试3次
     * - RemoteAccessException 异常触发重试
     * - 指数退避策略（1秒起步，2倍增长）
     */
    private void demonstrateBasicRetry(PaymentService paymentService) {
        System.out.println("\n为什么要重试？");
        System.out.println("答：在分布式系统中，网络波动、服务抖动、临时故障是常态。");
        System.out.println("    重试机制让系统在遇到临时性故障时自动恢复，提高可用性。\n");

        // 创建一个测试订单
        Order order = new Order();
        order.setOrderId("ORDER-001");
        order.setAmount(new BigDecimal("99.99"));
        order.setCreateTime(java.time.LocalDateTime.now());

        // 启用失败模拟，演示重试
        paymentService.setSimulateFailure(true);
        paymentService.reset();

        System.out.println("调用 PaymentService.processPayment()...");
        System.out.println("预期：由于 simulateFailure=true，前3次会失败并重试，第4次成功\n");

        String result = paymentService.processPayment(order);
        System.out.println("最终结果：" + result);
    }

    /**
     * 演示2：固定延迟重试策略
     *
     * 使用 FixedBackOffPolicy，每次重试间隔固定为2秒
     * 适用于对响应时间敏感、重试次数少的场景
     */
    private void demonstrateFixedDelayRetry(OrderService orderService) {
        System.out.println("\n固定延迟策略特点：");
        System.out.println("- 每次重试间隔固定（如每次都等2秒）");
        System.out.println("- 简单可预测");
        System.out.println("- 适用于轻量级调用或重试次数少的场景\n");

        orderService.resetAllCounters();

        System.out.println("创建订单（使用固定延迟2秒重试策略）...");
        Order order = orderService.createOrderWithFixedDelay(new BigDecimal("199.99"));
        System.out.println("创建结果：" + order);
    }

    /**
     * 演示3：指数退避重试策略
     *
     * 使用 ExponentialBackOffPolicy，重试间隔呈指数增长
     * delay=1000, multiplier=2, maxDelay=10000
     * 实际间隔：1s -> 2s -> 4s -> 8s (但最大不超过10s)
     *
     * 适用于远程API调用，可避免压垮下游服务
     */
    private void demonstrateExponentialBackoff(OrderService orderService) {
        System.out.println("\n指数退避策略特点：");
        System.out.println("- 每次重试间隔翻倍增长（如1s, 2s, 4s, 8s...）");
        System.out.println("- 给服务恢复更多时间");
        System.out.println("- 减少对下游服务的压力");
        System.out.println("- 适用于第三方API调用、数据库连接等\n");

        orderService.resetAllCounters();

        System.out.println("创建订单（使用指数退避策略）...");
        Order order = orderService.createOrderWithExponentialBackoff(new BigDecimal("299.99"));
        System.out.println("创建结果：" + order);
    }

    /**
     * 演示4：带回退机制的重试
     *
     * 当重试次数用尽后仍然失败，自动调用回退方法
     * 回退方法可以进行降级处理：
     * - 记录日志
     * - 发送告警
     * - 返回默认值或缓存数据
     * - 将任务加入重试队列
     */
    private void demonstrateRetryWithRecover(OrderService orderService) {
        System.out.println("\n带Recover回退机制的重试流程：");
        System.out.println("1. 调用方法 → 抛出异常");
        System.out.println("2. 满足重试条件 → 开始重试");
        System.out.println("3. 重试 N 次后仍然失败");
        System.out.println("4. 调用回退（Recovery）方法 → 执行降级处理");
        System.out.println("5. 返回降级处理结果\n");

        orderService.resetAllCounters();

        // 创建一个测试订单
        Order order = new Order();
        order.setOrderId("ORDER-RECOVER-001");
        order.setAmount(new BigDecimal("399.99"));
        order.setCreateTime(java.time.LocalDateTime.now());

        System.out.println("处理支付（启用回退机制）...");
        String result = orderService.processPaymentWithRecover(order);
        System.out.println("处理结果：" + result);
    }
}
