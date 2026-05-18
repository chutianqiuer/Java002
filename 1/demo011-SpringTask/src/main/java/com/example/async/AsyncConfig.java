package com.example.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步配置类
 *
 * 【功能说明】
 * 本配置类负责配置Spring异步执行相关的基础设施
 *
 * 【核心概念】
 *
 * 1. TaskExecutor接口
 *    - Spring的任务执行器接口
 *    - 类似于java.util.concurrent.Executor接口
 *    - 主要实现：ThreadPoolTaskExecutor（基于线程池）
 *
 * 2. @Async注解
 *    - 标注在方法上，使方法异步执行
 *    - 调用者不会阻塞，会立即返回
 *    - 方法会在独立线程中执行
 *
 * 3. @EnableAsync
 *    - 开启Spring异步方法支持
 *    - 需要配合@Bean配置的TaskExecutor使用
 *
 * 【@Async注解详解】
 *
 * 1. 作用范围
 *    - 可以标注在类上（所有public方法异步执行）
 *    - 可以标注在方法上（仅该方法异步执行）
 *    - 推荐标注在方法上，避免意外异步执行
 *
 * 2. 执行原理
 *    - Spring会为标注@Async的方法创建代理对象
 *    - 调用@Async方法时，代理对象会将任务提交到线程池
 *    - 原方法的执行在线程池的某个线程中进行
 *
 * 3. 重要限制：同类内部调用失效
 *    - 当一个方法调用同类中标注@Async的方法时
 *    - 由于是this调用，不会经过代理对象
 *    - 导致@Async注解失效，变成同步执行
 *    - 解决方案：
 *      a) 通过注入自身（@Autowired注入自己）
 *      b) 通过ApplicationContext获取Bean
 *      c) 使用Javassist、CGLIB等字节码工具（不推荐）
 *
 * 【线程池配置参数】
 *
 * 1. corePoolSize: 核心线程数
 *    - 线程池维持的最小线程数
 *    - 即使线程空闲，也不会回收
 *
 * 2. maxPoolSize: 最大线程数
 *    - 线程池允许的最大线程数
 *    - 当队列满时，会创建新线程直到达到最大线程数
 *
 * 3. queueCapacity: 队列容量
 *    - 用于存储等待执行任务的队列
 *    - 当核心线程都在忙碌，新任务会在队列中等待
 *    - 队列满后会创建新线程（直到maxPoolSize）
 *    - 队列也满后会触发拒绝策略
 *
 * 4. threadNamePrefix: 线程名称前缀
 *    - 方便在日志中识别异步任务线程
 *
 * 5. keepAliveSeconds: 线程空闲存活时间
 *    - 非核心线程空闲后存活时间
 *    - 超过这个时间会被回收
 *
 * 6. awaitTerminationSeconds: 等待终止时间
 *    - shutdown时等待任务完成的时间
 */
@Configuration  // 配置类
@EnableAsync    // 开启异步方法支持
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    /**
     * 配置异步任务执行器
     *
     * 【返回值的类型】
     * 返回java.util.concurrent.Executor接口
     * 这是Spring的AsyncConfigurer接口要求的方法
     *
     * 【方法名的重要性】
     * 方法名可以是任意名称
     * 但通常命名为taskExecutor或asyncExecutor
     *
     * @return Executor执行器
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        logger.info("配置异步任务执行器...");

        // 创建ThreadPoolTaskExecutor
        // 这是Spring提供的线程池执行器，基于java.util.concurrent包
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // ========== 线程池基本配置 ==========

        /**
         * 核心线程数
         * 线程池维护的最小线程数
         *
         * 【配置建议】
         * - CPU密集型任务（如计算）：CPU核心数 + 1
         * - IO密集型任务（如网络、数据库）：CPU核心数 * 2 或更多
         * - 混合型任务：需要根据实际情况调整
         *
         * 【为什么IO密集型需要更多线程？】
         * - IO操作会让线程阻塞，不占用CPU
         * - 阻塞时可以切换到其他任务执行
         * - 所以需要更多线程来充分利用CPU
         */
        executor.setCorePoolSize(10);

        /**
         * 最大线程数
         * 线程池允许的最大线程数
         *
         * 【何时达到最大线程数】
         * - 当队列（queueCapacity）满时
         * - 会创建新线程直到达到maxPoolSize
         *
         * 【配置建议】
         * - 通常设置为 corePoolSize 的 2 倍
         * - 或者根据任务特性调整
         */
        executor.setMaxPoolSize(20);

        /**
         * 队列容量
         * 等待执行的任务队列容量
         *
         * 【队列类型】
         * ThreadPoolTaskExecutor默认使用LinkedBlockingQueue
         * 这是一个无界队列（Integer.MAX_VALUE）
         *
         * 【配置建议】
         * - 如果任务量很大，建议设置合理的队列大小
         * - 避免任务无限堆积导致内存溢出
         */
        executor.setQueueCapacity(100);

        /**
         * 线程名称前缀
         * 异步线程的名称前缀，方便日志排查
         *
         * 【格式】
         * asyncExecutor-1, asyncExecutor-2, ...
         */
        executor.setThreadNamePrefix("asyncExecutor-");

        /**
         * 线程空闲存活时间
         * 非核心线程空闲后的存活时间
         *
         * 【作用】
         * - 减少资源消耗
         * - 当任务量减少时，回收多余的线程
         */
        executor.setKeepAliveSeconds(60);

        /**
         * 等待任务完成的时间
         * shutdown时等待所有任务完成的时间
         *
         * 【使用场景】
         * - 应用关闭时确保任务完成
         * - 避免任务被强制中断
         */
        executor.setAwaitTerminationSeconds(60);

        /**
         * 关闭时是否等待任务完成
         *
         * 【建议】
         * 生产环境建议设置为true
         */
        executor.setWaitForTasksToCompleteOnShutdown(true);

        /**
         * 线程池的饱和策略
         * 当线程池和队列都满时的处理策略
         *
         * 【可选策略】
         * 1. AbortPolicy（默认）：抛出RejectedExecutionException
         * 2. CallerRunsPolicy：由调用线程执行
         * 3. DiscardPolicy：静默丢弃
         * 4. DiscardOldestPolicy：丢弃最老的任务
         *
         * 【CallerRunsPolicy的好处】
         * - 不会丢失任务
         * - 会降低提交速度，给线程池喘息机会
         */
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化线程池
        // 这一步很重要！必须调用，否则线程池不会真正创建
        executor.initialize();

        logger.info("异步任务执行器配置完成：核心线程数={}，最大线程数={}，队列容量={}",
                    executor.getCorePoolSize(),
                    executor.getMaxPoolSize(),
                    executor.getQueueCapacity());

        return executor;
    }

    /**
     * 实现AsyncConfigurer接口的方法
     * 指定默认的异步执行器
     *
     * 【注意】
     * 这个方法的返回值类型是Executor
     * @Async注解默认使用这个执行器
     * 如果@Async指定了executor属性，则使用指定的执行器
     *
     * @return 默认的异步执行器
     */
    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }
}
