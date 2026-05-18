package com.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Task 任务调度与异步执行示例 - 主启动类
 *
 * 本示例演示Spring Framework中的任务调度和异步执行功能。
 *
 * 【核心概念】
 *
 * 1. 任务调度(Task Scheduling)
 *    - 概念：根据时间来执行任务的一种机制
 *    - Spring通过@Scheduled注解支持定时任务
 *    - 支持多种调度方式：固定延迟、固定频率、Cron表达式
 *    - 使用场景：数据备份、定时统计、缓存刷新、邮件发送等
 *
 * 2. 异步执行(Async Execution)
 *    - 概念：不在主线程中执行任务，而是在线程池中异步执行
 *    - Spring通过@Async注解支持异步方法调用
 *    - 提高程序并发性，避免阻塞主线程
 *    - 使用场景：耗时操作、远程调用、批量处理等
 *
 * 【关键注解】
 *
 * @EnableScheduling - 开启基于注解的定时任务调度
 *   位置：通常放在配置类或启动类上
 *   作用：启用Spring对@Scheduled注解的识别和处理
 *   注意：不添加此注解，@Scheduled不会生效
 *
 * @EnableAsync - 开启异步方法执行支持
 *   位置：通常放在配置类或启动类上
 *   作用：启用Spring对@Async注解的识别和处理
 *   配合：需要配合TaskExecutor（任务执行器）使用
 *
 * 【Spring版本说明】
 * 本示例基于Spring 6.x，需要Java 17以上版本
 * 如果使用Spring 5.x，可以使用Java 8以上版本
 */
@ComponentScan(basePackages = "com.example") // 组件扫描，扫描com.example包下的所有组件
@EnableScheduling                             // 开启定时任务调度功能
@EnableAsync                                  // 开启异步方法执行功能
public class MainApp {

    /**
     * 主方法 - 程序入口
     *
     * Spring Task 示例程序入口
     * 演示内容：
     * 1. 定时任务调度 - @Scheduled注解的使用
     * 2. 异步方法执行 - @Async注解的使用
     * 3. 任务执行器 - TaskExecutor接口
     * 4. Cron表达式 - 复杂时间调度
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Spring Task 任务调度与异步执行示例启动中...   ");
        System.out.println("=================================================\n");

        // 创建Spring应用上下文
        // AnnotationConfigApplicationContext: 基于注解配置的Spring应用上下文
        // 会自动扫描@EnableScheduling和@EnableAsync注解
        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(MainApp.class);

        System.out.println("\n=================================================");
        System.out.println("  Spring容器启动完成！                          ");
        System.out.println("=================================================\n");

        // 打印项目说明
        printProjectInfo();

        // 阻塞主线程，避免立即退出
        // 让定时任务和异步任务有足够时间执行
        System.out.println("\n>>> 主线程继续执行中，定时任务和异步任务将在后台运行... <<<\n");
        System.out.println(">>> 按 Ctrl+C 可以停止程序 <<<\n");

        try {
            // 主线程睡眠一段时间，让各个任务有足够时间执行
            // 实际项目中，主线程通常会持续运行（如Tomcat服务器）
            Thread.sleep(60000); // 60秒后自动退出
        } catch (InterruptedException e) {
            System.out.println("主线程被中断，程序退出");
            Thread.currentThread().interrupt();
        }

        System.out.println("\n=================================================");
        System.out.println("  程序运行结束                                  ");
        System.out.println("=================================================");

        // 关闭Spring容器
        context.close();
    }

    /**
     * 打印项目说明信息
     * 帮助理解本示例要演示的内容
     */
    private static void printProjectInfo() {
        System.out.println("【项目结构说明】");
        System.out.println("----------------------------------------------------");
        System.out.println("com.example.config.TaskConfig    - 任务调度配置类");
        System.out.println("com.example.task.SimpleScheduledTask - 固定频率/延迟任务");
        System.out.println("com.example.task.CronScheduledTask   - Cron表达式任务");
        System.out.println("com.example.task.DynamicScheduledTask - 动态任务调度");
        System.out.println("com.example.async.AsyncService       - 异步服务");
        System.out.println("com.example.async.AsyncConfig        - 异步配置");
        System.out.println("----------------------------------------------------\n");

        System.out.println("【演示任务列表】");
        System.out.println("----------------------------------------------------");
        System.out.println("1. SimpleScheduledTask - 每3秒执行一次的定时任务(fixedDelay)");
        System.out.println("2. SimpleScheduledTask - 每2秒执行一次的定时任务(fixedRate)");
        System.out.println("3. CronScheduledTask   - 每分钟第30秒执行的任务(cron表达式)");
        System.out.println("4. DynamicScheduledTask - 动态添加和取消任务");
        System.out.println("5. AsyncService        - 异步方法调用示例");
        System.out.println("6. AsyncService        - 异步方法返回值示例(Future)");
        System.out.println("----------------------------------------------------\n");
    }
}
