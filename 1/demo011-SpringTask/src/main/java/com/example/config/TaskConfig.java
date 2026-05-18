package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Spring Task 任务调度配置类
 *
 * 【功能说明】
 * 本配置类负责配置Spring Task相关的基础设施组件
 *
 * 【核心概念 - TaskScheduler】
 * TaskScheduler是Spring任务调度的核心接口，负责执行定时任务
 * 它的主要作用是根据@Scheduled注解配置的时间规则，准时触发任务的执行
 *
 * 【任务调度架构】
 * 1. @Scheduled注解：标记方法为定时任务，并配置执行时间规则
 * 2. TaskScheduler：任务调度器，负责管理任务的调度和执行
 * 3. Task：实际要执行的任务对象
 * 4. Trigger：触发器，根据时间规则决定任务何时执行
 *
 * 【ThreadPoolTaskScheduler详解】
 * ThreadPoolTaskScheduler是TaskScheduler的常用实现类
 * - 内部维护一个线程池来执行定时任务
 * - 支持动态添加/移除任务
 * - 支持多种触发策略（fixedDelay、fixedRate、cron）
 *
 * 【线程池配置参数】
 * - poolSize: 线程池大小，决定同时可以执行多少个定时任务
 *   * 如果定时任务较多，需要适当增加线程池大小
 *   * 如果任务执行时间较长，需要考虑任务的性质调整线程数
 * - threadNamePrefix: 线程名称前缀，方便日志排查
 * - awaitTerminationSeconds: 等待线程池关闭的最大时间
 * - waitForTasksToCompleteOnShutdown: 关闭时是否等待任务完成
 *
 * 【注意事项】
 * 1. 线程池大小要合理设置，太小会导致任务排队等待
 * 2. 如果任务有共享资源，需要注意线程安全问题
 * 3. 定时任务的执行时间不应超过调度间隔，否则可能造成任务堆积
 */
@Configuration  // 标识为配置类，Spring会自动扫描并注册其中的@Bean方法
public class TaskConfig {

    /**
     * 配置任务调度器(TaskScheduler)
     *
     * TaskScheduler是Spring 3.0引入的接口，提供了更灵活的任务调度能力
     * 它是@Scheduled注解工作的基础
     *
     * 【工作流程】
     * 1. Spring容器启动时，扫描所有带@Scheduled注解的方法
     * 2. 为每个@Scheduled方法注册一个任务到TaskScheduler
     * 3. TaskScheduler根据配置的时间规则，准时触发任务执行
     * 4. 任务在线程池的某个线程中执行
     *
     * @return ThreadPoolTaskScheduler实例
     */
    @Bean
    public TaskScheduler taskScheduler() {
        // 创建ThreadPoolTaskScheduler实例
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // ========== 线程池基本配置 ==========

        /**
         * 线程池大小
         * 决定同时可以执行多少个定时任务
         *
         * 【设置原则】
         * - CPU密集型任务：线程数 = CPU核心数 + 1
         * - IO密集型任务：线程数 = CPU核心数 * 2 或更多
         * - 定时任务通常涉及IO操作（如数据库、网络），可以适当增加线程数
         *
         * 【注意事项】
         * - 线程数过多会导致上下文切换开销增加
         * - 线程数过少会导致任务排队，影响时效性
         */
        scheduler.setPoolSize(10);

        /**
         * 线程名称前缀
         * 所有由该调度器创建的线程都会以此作为名称前缀
         * 格式：taskScheduler-1, taskScheduler-2, ...
         *
         * 【用途】
         * - 方便在日志中识别任务调度相关的线程
         * - 方便在调试时定位线程问题
         */
        scheduler.setThreadNamePrefix("taskScheduler-");

        /**
         * 等待任务完成的时间（秒）
         * 当调用shutdown()时，会等待正在执行的任务在指定时间内完成
         *
         * 【使用场景】
         * - 应用关闭时，确保正在执行的定时任务不会立即被中断
         * - 避免数据不一致或任务半途而废
         */
        scheduler.setAwaitTerminationSeconds(60);

        /**
         * 关闭时是否等待任务完成
         * true: 调用shutdown()时，会等待所有任务执行完成
         * false: 立即停止，可能导致任务被中断
         *
         * 【建议】
         * 生产环境建议设置为true，确保任务完整性
         */
        scheduler.setWaitForTasksToCompleteOnShutdown(true);

        /**
         * 任务拒绝处理器
         * 当线程池已满，且等待队列也满时，如何处理新提交的任务
         *
         * 【可选策略】
         * - AbortPolicy(默认): 抛出RejectedExecutionException
         * - CallerRunsPolicy: 由调用线程执行
         * - DiscardPolicy: 静默丢弃
         * - DiscardOldestPolicy: 丢弃最老的任务
         *
         * 【注意】
         * 定时任务的拒绝策略比较特殊，因为任务通常有时间要求
         * 如果任务被拒绝，可能需要人工介入或告警
         */
        // scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化调度器
        // 这一步会创建线程池
        scheduler.initialize();

        return scheduler;
    }
}
