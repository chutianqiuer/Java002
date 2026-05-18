# Spring Retry 重试机制深入学习示例

## 项目简介

本项目是一个 Spring Retry 重试机制的深入学习示例，通过实际代码演示了：

- 为什么需要重试机制
- `@EnableRetry` 开启重试功能
- `@Retryable` 注解的核心属性配置
- 固定延迟与指数退避两种重试策略
- `@Recover` 注解定义回退方法
- 熔断回退的完整流程

## 目录结构

```
demo010-SpringRetry/
├── pom.xml                                    # Maven 项目配置文件
├── src/main/java/com/example/
│   ├── Demo010Application.java                # Spring Boot 启动类
│   ├── config/
│   │   └── RetryConfig.java                   # 重试配置类（@EnableRetry）
│   ├── service/
│   │   ├── PaymentService.java                # 支付服务（基础重试演示）
│   │   └── OrderService.java                  # 订单服务（多种重试策略）
│   ├── model/
│   │   └── Order.java                         # 订单实体类
│   └── recover/
│       └── PaymentRecoverCallback.java        # 回退方法示例
└── README.md                                  # 项目说明文档
```

## 核心概念

### 1. 为什么需要重试机制？

在分布式系统中，临时性故障是常态：

| 故障类型 | 示例 | 处理方式 |
|---------|------|---------|
| 网络抖动 | 请求超时、网络中断 | 重试 |
| 服务繁忙 | 第三方API限流 | 指数退避重试 |
| 资源瞬时不可用 | 连接池耗尽、GC停顿 | 短暂等待后重试 |
| 节点不健康 | 负载均衡分发到故障节点 | 重试到健康节点 |

### 2. @EnableRetry

在配置类上添加 `@EnableRetry` 注解，开启 Spring Retry 功能：

```java
@Configuration
@EnableRetry
public class RetryConfig {
    // ...
}
```

### 3. @Retryable 注解属性

```java
@Retryable(
    value = RemoteAccessException.class,    // 指定重试的异常类型
    maxAttempts = 3,                        // 最大重试次数（默认3次）
    backoff = @Backoff(
        delay = 1000,                        // 初始延迟（毫秒）
        multiplier = 2.0,                   // 延迟倍数（指数退避）
        maxDelay = 10000                     // 最大延迟（毫秒）
    ),
    recover = "fallbackMethod"              // 重试失败后的回调方法
)
public String callRemoteService() {
    // 业务逻辑
}
```

### 4. 重试策略对比

| 策略 | 延迟计算公式 | 适用场景 | 优缺点 |
|------|-------------|---------|--------|
| 固定延迟 (FixedBackOff) | delay | 重试次数少、延迟敏感 | 简单但可能压垮服务 |
| 指数退避 (ExponentialBackOff) | delay × multiplier^n | 第三方API、远程调用 | 给服务恢复时间，但初始响应慢 |

### 5. 完整重试流程

```
┌─────────────────────────────────────────────────────────────┐
│                      调用 processPayment()                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  第1次尝试调用    │
                    └─────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
                  成功                失败（RemoteAccessException）
                    │                   │
                    ▼                   ▼
              返回成功           等待 1s（delay=1000）
                                          │
                                          ▼
                                ┌─────────────────┐
                                │  第2次尝试调用    │
                                └─────────────────┘
                                          │
                    ┌─────────────────────┴─────────────────────┐
                    │                                       │
                  成功                                    失败
                    │                                       │
                    ▼                               等待 2s（1000×2）
              返回成功                                         │
                                                            ▼
                                                  ┌─────────────────┐
                                                  │  第3次尝试调用    │
                                                  └─────────────────┘
                                                            │
                                          ┌─────────────────┴─────────────────┐
                                          │                                   │
                                        成功                                失败
                                          │                                   │
                                          ▼                         所有重试次数用尽
                                    返回成功                                    │
                                                                    ▼
                                                        ┌─────────────────────┐
                                                        │ 调用 @Recover 回退方法 │
                                                        │ (fallback method)    │
                                                        └─────────────────────┘
                                                                    │
                                                                    ▼
                                                             返回降级处理结果
```

## 运行项目

### 环境要求

- JDK 8 或更高版本
- Maven 3.6+

### 编译项目

```bash
cd demo010-SpringRetry
mvn clean compile
```

### 运行演示

```bash
mvn spring-boot:run
```

或者直接运行 main 方法：

```bash
mvn clean package
java -jar target/demo010-SpringRetry-1.0.0.jar
```

### 查看输出

运行后会看到以下演示内容：

1. **基本重试机制演示** - PaymentService 的重试流程
2. **固定延迟重试策略** - 每隔固定2秒重试
3. **指数退避重试策略** - 重试间隔呈指数增长
4. **带回退机制的重试** - 重试失败后执行降级处理

## 关键代码说明

### PaymentService - 基础重试

```java
@Retryable(
    value = RemoteAccessException.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2.0)
)
public String processPayment(Order order) {
    // 模拟可能失败的远程调用
}
```

### PaymentRecoverCallback - 回退方法

```java
@Component
public class PaymentRecoverCallback {
    // 回退方法的参数：第一个是异常类型，后续是原始方法参数
    public Order recoverFromRemoteAccessException(RemoteAccessException e, Order order) {
        // 降级处理逻辑
        order.setStatus(3); // 标记为失败
        order.setDescription("支付失败 - 待人工处理");
        return order;
    }
}
```

## 注意事项

1. **幂等性**：重试机制要求操作是幂等的，否则可能导致数据问题
2. **重试次数**：不要设置过多重试次数，避免长时间阻塞
3. **延迟策略**：根据业务场景选择合适的退避策略
4. **回退方法**：务必提供回退方法，避免用户看到未处理的异常

## 参考资料

- [Spring Retry 官方文档](https://github.com/spring-projects/spring-retry)
- [Spring Retry GitHub](https://github.com/spring-projects/spring-retry)
