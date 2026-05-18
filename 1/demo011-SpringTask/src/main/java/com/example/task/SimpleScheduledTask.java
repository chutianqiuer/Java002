package com.example.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单定时任务示例 - 演示@Scheduled注解的fixedDelay和fixedRate
 *
 * 【功能说明】
 * 本类演示Spring Task中最基本的定时任务配置方式
 *
 * 【@Scheduled注解详解】
 * @Scheduled是Spring提供的用于声明式定时任务的注解
 * 可以标注在方法上，使其成为一个定时执行的任务
 *
 * 【限制】
 * @Scheduled只能用于没有返回值的方法，或者返回void的方法
 * 如果需要返回值，需要使用TaskScheduler接口的schedule()方法
 *
 * 【时间配置方式】
 * 1. fixedDelay: 固定延迟时间，上次执行完毕后隔多长时间再执行
 * 2. fixedRate: 固定频率，上次开始执行后隔多长时间再执行
 * 3. initialDelay: 初始延迟，容器启动后延迟多长时间开始执行
 * 4. cron: Cron表达式，灵活的时间配置
 *
 * 【fixedDelay vs fixedRate 的区别】
 *
 * fixedDelay（固定延迟）：
 * - 语义：上次执行完毕后，隔N毫秒再执行
 * - 计算公式：下次执行时间 = 上次执行结束时间 + delay值
 * - 适用场景：任务执行时间不确定，需要确保任务不堆积
 * - 示例：fixedDelay = 3000 表示上一次执行完成后，等3秒再执行下一次
 *
 * fixedRate（固定频率）：
 * - 语义：上次开始执行后，隔N毫秒再执行
 * - 计算公式：下次执行时间 = 上次执行开始时间 + rate值
 * - 适用场景：任务执行时间比较稳定，希望按照固定频率执行
 * - 注意：如果任务执行时间 > rate值，会导致任务连续执行（追赶模式）
 * - 示例：fixedRate = 2000 表示上一次开始执行后，等2秒就开始下一次（即使上一次还没完成）
 *
 * 【代码示例解析】
 * 1. printCurrentTimeFixedDelay: 每次执行完后等3秒
 * 2. printCurrentTimeFixedRate: 每隔2秒就开始执行一次（不管上次完成没有）
 */
@Component  // 标注为Spring组件，会被自动扫描并注册到容器
public class SimpleScheduledTask {

    // 使用SLF4J日志框架
    // 相比System.out.println，日志框架更适合生产环境
    private static final Logger logger = LoggerFactory.getLogger(SimpleScheduledTask.class);

    // 日期时间格式化器，用于输出可读的时间字符串
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    // 计数器，用于记录任务执行次数
    // 使用AtomicInteger保证线程安全
    private final AtomicInteger fixedDelayCounter = new AtomicInteger(0);
    private final AtomicInteger fixedRateCounter = new AtomicInteger(0);

    /**
     * 任务1：fixedDelay 示例
     *
     * 【@Scheduled(fixedDelay = 3000) 详解】
     * - fixedDelay: 固定延迟时间，单位毫秒
     * - 3000: 3000毫秒 = 3秒
     * - 语义：上次执行完毕后，等待3秒，再执行下一次
     *
     * 【执行时机】
     * 假设任务在10:00:00开始执行，10:00:02执行完成
     * 则下一次执行时间 = 10:00:02 + 3秒 = 10:00:05
     *
     * 【特点】
     * - 任务完成后才开始计算下一次执行时间
     * - 可以确保任务之间有足够的间隔
     * - 适合任务执行时间不确定的场景
     *
     * 【initialDelay 初始延迟】
     * - 容器启动后，延迟多长时间才开始第一次执行
     * - 本例中：容器启动后等5秒才开始第一次执行
     * - 作用：避免容器启动时瞬间压力过大
     */
    @Scheduled(fixedDelay = 3000, initialDelay = 5000)
    public void printCurrentTimeFixedDelay() {
        // 获取当前执行次数
        int count = fixedDelayCounter.incrementAndGet();

        // 记录任务开始执行的时间
        long startTime = System.currentTimeMillis();

        // 获取当前时间并格式化
        String currentTime = LocalDateTime.now().format(formatter);

        // 打印日志
        logger.info("[fixedDelay任务] 第{}次执行，当前时间：{}，线程名称：{}",
                    count, currentTime, Thread.currentThread().getName());

        // 模拟任务执行时间
        // 假设这个任务需要执行2秒
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("任务被中断", e);
        }

        // 计算任务执行耗时
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("[fixedDelay任务] 第{}次执行完成，耗时：{}ms", count, executionTime);

        /*
         * 【fixedDelay执行分析】
         *
         * 第1次：10:00:05开始 -> 10:00:07完成
         * 第2次：10:00:07(完成) + 3秒 = 10:00:10开始
         * 第3次：10:00:12(完成) + 3秒 = 10:00:15开始
         * ...
         *
         * 可以看到，fixedDelay确保了任务完成到下次开始之间有固定的间隔
         */
    }

    /**
     * 任务2：fixedRate 示例
     *
     * 【@Scheduled(fixedRate = 2000) 详解】
     * - fixedRate: 固定频率，单位毫秒
     * - 2000: 2000毫秒 = 2秒
     * - 语义：上次开始执行后，等待2秒，再执行下一次
     *
     * 【执行时机】
     * 假设任务在10:00:00开始执行
     * 则下一次执行时间 = 10:00:00 + 2秒 = 10:00:02
     * （注意：不管任务是否在2秒内完成）
     *
     * 【特点】
     * - 任务开始后就开始计算下一次执行时间
     * - 如果任务执行时间 > rate值，会造成任务堆积
     * - 适合任务执行时间比较稳定的场景
     *
     * 【潜在问题：任务堆积】
     * 假设fixedRate = 2000，但任务执行需要3秒：
     * 10:00:00 - 第1次开始执行
     * 10:00:02 - 第2次应该开始（但第1次还在执行）
     * 10:00:03 - 第1次执行完成
     * 10:00:04 - 第2次执行完成（连续执行，中间无间隔）
     * 10:00:06 - 第3次应该开始
     * ...
     *
     * 可以看到，如果任务执行时间超过rate，任务会"追赶"执行
     */
    @Scheduled(fixedRate = 2000)
    public void printCurrentTimeFixedRate() {
        int count = fixedRateCounter.incrementAndGet();
        long startTime = System.currentTimeMillis();

        String currentTime = LocalDateTime.now().format(formatter);

        logger.info("[fixedRate任务] 第{}次执行，当前时间：{}，线程名称：{}",
                    count, currentTime, Thread.currentThread().getName());

        // 模拟任务执行时间为1秒（小于rate=2秒，所以不会堆积）
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("任务被中断", e);
        }

        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("[fixedRate任务] 第{}次执行完成，耗时：{}ms", count, executionTime);

        /*
         * 【fixedRate执行分析】
         *
         * 第1次：10:00:00开始 -> 10:00:01完成
         * 第2次：10:00:00(开始) + 2秒 = 10:00:02开始
         * 第3次：10:00:02(开始) + 2秒 = 10:00:04开始
         * ...
         *
         * 因为任务执行时间(1秒) < rate(2秒)，
         * 所以任务可以按照每2秒一次的频率稳定执行
         */
    }

    /**
     * 任务3：initialDelay 示例
     *
     * 【initialDelay 详解】
     * - 作用：指定容器启动后延迟多长时间才开始第一次执行
     * - 单位：毫秒
     * - 适用场景：
     *   1. 容器启动时需要初始化其他资源
     *   2. 避免启动时多个任务同时执行造成压力
     *   3. 等待依赖的服务启动完成
     *
     * 【配置技巧】
     * initialDelay可以和fixedDelay或fixedRate配合使用
     * initialDelay只影响第一次执行，之后就按照fixedDelay或fixedRate执行
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 10000)  // 启动后10秒才开始，每5秒执行一次
    public void taskWithInitialDelay() {
        int count = fixedDelayCounter.incrementAndGet();
        String currentTime = LocalDateTime.now().format(formatter);

        logger.info("[initialDelay任务] 第{}次执行，当前时间：{}，线程名称：{}",
                    count, currentTime, Thread.currentThread().getName());
    }

    /**
     * 任务4：演示fixedDelay和fixedRate同时配置时的行为
     *
     * 【注意】
     * @Scheduled注解中fixedDelay和fixedRate不能同时使用
     * 只能选择其中一种配置方式
     *
     * 如果需要动态切换，可以使用TaskScheduler接口手动调度任务
     * （参见DynamicScheduledTask类）
     */
    // @Scheduled(fixedDelay = 3000, fixedRate = 2000)  // 这是错误的配置！

    /**
     * 获取任务执行统计信息
     * 用于监控和调试
     */
    public String getTaskStats() {
        return String.format("fixedDelay任务执行次数: %d, fixedRate任务执行次数: %d",
                fixedDelayCounter.get(), fixedRateCounter.get());
    }
}
