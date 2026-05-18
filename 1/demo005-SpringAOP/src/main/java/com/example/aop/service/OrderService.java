package com.example.aop.service;

/**
 * 订单服务类 - 演示AOP在不同业务场景中的应用
 *
 * 本类与UserService配合使用，主要用于展示：
 * 1. 同一个切面（LoggingAspect）如何同时拦截多个类
 * 2. 切入点表达式如何精确匹配特定类的方法
 * 3. AOP在实际业务中的应用场景
 *
 * AOP应用场景说明：
 * 1. 日志记录：在每个业务方法执行前后记录日志
 * 2. 性能监控：统计每个方法的执行时间
 * 3. 事务管理：在方法开始时开启事务，提交或回滚
 * 4. 权限校验：验证用户是否有权限执行该操作
 * 5. 异常处理：统一处理业务方法抛出的异常
 */
public class OrderService {

    /**
     * 创建订单
     *
     * 切入点匹配：本方法会被LoggingAspect拦截
     * 因为切入点表达式使用了 execution(* com.example.aop.service.*.*(..))
     * 匹配了service包下所有类的所有方法
     *
     * @param userId 用户ID
     * @param productName 商品名称
     * @param price 价格
     * @return 订单号
     */
    public String createOrder(Long userId, String productName, double price) {
        System.out.println("[OrderService] 执行createOrder方法");
        System.out.println("  用户ID：" + userId);
        System.out.println("  商品名称：" + productName);
        System.out.println("  价格：" + price);

        // 模拟订单创建逻辑
        String orderId = "ORDER" + System.currentTimeMillis();
        System.out.println("[OrderService] 订单创建成功，订单号：" + orderId);
        return orderId;
    }

    /**
     * 查询订单
     *
     * 切入点匹配：会被PerformanceAspect拦截进行性能监控
     * 通过@Around通知记录方法执行时间
     *
     * @param orderId 订单号
     * @return 订单信息字符串
     */
    public String queryOrder(String orderId) {
        System.out.println("[OrderService] 执行queryOrder方法，订单号：" + orderId);

        // 模拟数据库查询
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("订单号不能为空");
        }

        // 模拟查询到的订单信息
        String orderInfo = "订单号：" + orderId + "，状态：已支付，创建时间：2024-01-01 10:00:00";
        System.out.println("[OrderService] 查询结果：" + orderInfo);
        return orderInfo;
    }

    /**
     * 取消订单
     *
     * 异常演示：本方法在特定条件下会抛出异常
     * 异常会被ValidationAspect的@AfterThrowing捕获
     *
     * @param orderId 订单号
     * @param reason 取消原因
     * @return 是否取消成功
     */
    public boolean cancelOrder(String orderId, String reason) {
        System.out.println("[OrderService] 执行cancelOrder方法");
        System.out.println("  订单号：" + orderId);
        System.out.println("  取消原因：" + reason);

        // 模拟取消订单逻辑
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("订单号不能为空");
        }

        if (!orderId.startsWith("ORDER")) {
            throw new RuntimeException("无效的订单号格式：" + orderId);
        }

        System.out.println("[OrderService] 订单取消成功：" + orderId);
        return true;
    }

    /**
     * 支付订单
     *
     * 事务演示：本方法需要事务支持
     * 在实际项目中，应该使用@Transactional注解
     * 这里我们通过PerformanceAspect展示性能监控
     *
     * @param orderId 订单号
     * @param amount 支付金额
     * @return 支付是否成功
     */
    public boolean payOrder(String orderId, double amount) {
        System.out.println("[OrderService] 执行payOrder方法");
        System.out.println("  订单号：" + orderId);
        System.out.println("  支付金额：" + amount);

        // 模拟支付逻辑
        if (orderId == null || amount <= 0) {
            throw new IllegalArgumentException("订单号或支付金额无效");
        }

        // 模拟支付网关调用
        System.out.println("[OrderService] 调用支付网关...");
        boolean success = amount > 0; // 简化逻辑

        if (success) {
            System.out.println("[OrderService] 支付成功");
        } else {
            System.out.println("[OrderService] 支付失败");
        }

        return success;
    }

    /**
     * 退款订单
     *
     * 异常处理演示：本方法抛出异常时会被ValidationAspect捕获
     *
     * @param orderId 订单号
     * @param refundAmount 退款金额
     * @return 退款是否成功
     */
    public boolean refundOrder(String orderId, double refundAmount) {
        System.out.println("[OrderService] 执行refundOrder方法");
        System.out.println("  订单号：" + orderId);
        System.out.println("  退款金额：" + refundAmount);

        // 模拟退款逻辑
        if (orderId == null || refundAmount <= 0) {
            throw new IllegalArgumentException("订单号或退款金额无效");
        }

        if (refundAmount > 10000) {
            throw new RuntimeException("单笔退款金额超过限额：最大10000元");
        }

        System.out.println("[OrderService] 退款成功：" + refundAmount + "元");
        return true;
    }
}
