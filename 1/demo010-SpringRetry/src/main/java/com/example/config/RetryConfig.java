package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Spring Retry 配置类
 *
 * 什么是 @EnableRetry？
 * @EnableRetry 是 Spring Retry 提供的注解，用于启用基于注解的重试功能。
 * 简单来说，它就像一个"开关"，告诉 Spring 框架：
 * "请扫描带有 @Retryable 注解的方法，当它们抛出异常时自动进行重试"
 *
 * 为什么需要这个配置？
 * Spring Retry 默认是不开启的。需要在配置类或启动类上添加 @EnableRetry
 * 才能让 @Retryable 和 @Backoff 等注解生效。
 *
 * @EnableRetry 的位置：
 * - 可以放在 @Configuration 类上（推荐，如本例）
 * - 也可以放在 @SpringBootApplication 启动类上
 *
 * 本配置类主要用于集中管理重试相关的配置
 */
@Configuration
@EnableRetry
public class RetryConfig {

    /**
     * 这里可以添加更多的重试配置 Bean
     *
     * 例如：
     * 1. 自定义的 RetryTemplate（更高级的重试模板）
     * 2. 重试监听器（用于监控重试行为）
     * 3. 不同的 BackOffPolicy（退避策略）
     *
     * 但对于大多数场景，只需要 @EnableRetry 就足够了，
     * 因为 Spring Retry 为我们提供了合理的默认配置。
     */

    /*
     * 补充知识：Spring Retry 的核心组件
     *
     * 1. RetryTemplate
     *    - Spring Retry 提供的核心类，用于编程式执行重试
     *    - 适合需要精细控制重试行为的场景
     *    - 示例：retryTemplate.execute(retryContext -> { ... });
     *
     * 2. BackOffPolicy（退避策略）
     *    - 定义两次重试之间的等待时间
     *    - FixedBackOffPolicy：固定延迟（如每次都等1秒）
     *    - ExponentialBackOffPolicy：指数延迟（如1秒、2秒、4秒...）
     *    - UniformRandomBackOffPolicy：随机延迟
     *
     * 3. RetryPolicy（重试策略）
     *    - 决定哪些异常应该触发重试
     *    - SimpleRetryPolicy：简单的基于异常类型的重试策略
     *    - TimeoutRetryPolicy：超时重试策略
     *    - ExpressionRetryPolicy：基于表达式判断是否重试
     *
     * 4. RetryListener
     *    - 重试监听器，可以在重试开始/结束/失败时执行回调
     *    - 用于日志记录、监控等场景
     */
}
