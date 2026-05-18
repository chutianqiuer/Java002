package com.example.task;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 动态任务调度示例
 *
 * 【功能说明】
 * 本类演示如何动态地添加、取消和修改定时任务
 * 弥补@Scheduled注解在运行时不支持修改的不足
 *
 * 【为什么需要动态任务调度？】
 *
 * 1. @Scheduled的局限性：
 *    - @Scheduled是声明式的，配置好后不能修改
 *    - 所有参数都是编译时确定的
 *    - 无法根据运行时条件动态调整
 *
 * 2. 动态任务调度的应用场景：
 *    - 用户可以在界面上配置定时任务
 *    - 根据系统负载动态调整任务执行频率
 *    - 实现任务的暂停、恢复、取消功能
 *    - 任务的可视化管理
 *
 * 【核心接口：TaskScheduler】
 *
 * TaskScheduler是Spring 3.0引入的任务调度接口
 * 主要方法：
 * - schedule(Runnable, Trigger): 执行一次性任务
 * - schedule(Runnable, Date): 在指定时间执行一次
 * - schedule(Runnable, Duration): 延迟一段时间后执行
 * - scheduleAtFixedRate(Runnable, Duration): 固定频率执行
 * - scheduleWithFixedDelay(Runnable, Duration): 固定延迟执行
 *
 * 【Trigger接口】
 * Trigger是触发器接口，用于确定任务的执行时间
 * Spring提供了两个实现：
 * - CronTrigger: 基于Cron表达式的触发器
 * - PeriodicTrigger: 基于时间间隔的触发器
 *
 * 【ScheduledFuture】
 * - 是Future接口的扩展
 * - 用于跟踪动态调度任务的状态
 * - 支持取消任务、判断任务是否完成等操作
 */
@Component
public class DynamicScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(DynamicScheduledTask.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * TaskScheduler: Spring的任务调度器
     * 由Spring自动注入，配置参见TaskConfig类
     */
    private final TaskScheduler taskScheduler;

    /**
     * 存储正在运行的任务
     * key: 任务名称
     * value: ScheduledFuture，可以控制任务的取消、暂停等
     *
     * 【为什么需要保存ScheduledFuture？】
     * - 需要能够取消或暂停正在运行的任务
     * - 需要查询任务的状态（是否完成、是否取消）
     * - 需要获取任务的执行结果（如果任务有返回值）
     */
    private final Map<String, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<>();

    /**
     * 计数器，用于生成唯一任务名称
     */
    private int taskCounter = 0;

    /**
     * 构造函数注入TaskScheduler
     *
     * 【注入方式说明】
     * Spring会自动注入TaskScheduler实例
     * 这个实例是在TaskConfig类中配置的Bean
     *
     * @param taskScheduler 任务调度器
     */
    public DynamicScheduledTask(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * 容器初始化后自动执行
     *
     * 【@PostConstruct】
     * 该注解标注的方法会在构造方法执行后自动调用
     * 用于执行初始化操作
     */
    @PostConstruct
    public void init() {
        logger.info("动态任务调度示例初始化...");
        // 添加一些初始任务
        addCronTask("初始任务-每10秒", "0/10 * * * * ?");
        addFixedDelayTask("初始任务-固定延迟5秒", 5000);
    }

    /**
     * 容器销毁前自动执行
     *
     * 【@PreDestroy】
     * 该注解标注的方法会在容器销毁前自动调用
     * 用于执行清理操作，如取消所有正在运行的任务
     */
    @PreDestroy
    public void cleanup() {
        logger.info("清理所有动态任务...");
        // 取消所有正在运行的任务
        runningTasks.forEach((name, future) -> {
            boolean cancelled = future.cancel(true);
            logger.info("任务 '{}' 取消{}",
                    name, cancelled ? "成功" : "失败（可能已完成）");
        });
        runningTasks.clear();
    }

    /**
     * 动态添加基于Cron表达式的任务
     *
     * 【使用场景】
     * - 用户通过界面配置定时任务
     * - 需要动态调整任务的执行时间
     * - 实现可配置的定时任务
     *
     * 【CronTrigger】
     * CronTrigger是Trigger接口的实现类
     * 接受Cron表达式作为调度规则
     *
     * @param taskName 任务名称（用于标识和取消任务）
     * @param cronExpression Cron表达式
     * @return 任务是否添加成功
     */
    public boolean addCronTask(String taskName, String cronExpression) {
        logger.info("添加Cron任务：{}，表达式：{}", taskName, cronExpression);

        // 如果任务已存在，先取消
        if (runningTasks.containsKey(taskName)) {
            cancelTask(taskName);
        }

        // 创建Runnable任务
        Runnable task = () -> {
            String currentTime = LocalDateTime.now().format(formatter);
            logger.info("[动态Cron任务-{}] 执行，当前时间：{}，线程：{}",
                        taskName, currentTime, Thread.currentThread().getName());
        };

        try {
            // 创建CronTrigger
            // CronTrigger会根据Cron表达式计算下次执行时间
            CronTrigger trigger = new CronTrigger(cronExpression);

            // 调度任务
            // schedule方法返回一个ScheduledFuture，可以用来控制任务的执行
            ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);

            // 保存任务引用，以便后续管理
            runningTasks.put(taskName, future);

            logger.info("Cron任务 '{}' 添加成功", taskName);
            return true;
        } catch (Exception e) {
            logger.error("添加Cron任务 '{}' 失败：{}", taskName, e.getMessage());
            return false;
        }
    }

    /**
     * 动态添加固定延迟任务
     *
     * 【fixedDelay vs fixedRate】
     * - fixedDelay: 上次执行完成后，等待指定时间再执行
     * - fixedRate: 上次开始执行后，等待指定时间再执行
     *
     * 【使用场景】
     * - 适合执行时间不确定的任务
     * - 确保任务之间有足够的间隔
     *
     * @param taskName 任务名称
     * @param delayMilliseconds 延迟时间（毫秒）
     * @return 任务是否添加成功
     */
    public boolean addFixedDelayTask(String taskName, long delayMilliseconds) {
        logger.info("添加固定延迟任务：{}，延迟：{}ms", taskName, delayMilliseconds);

        if (runningTasks.containsKey(taskName)) {
            cancelTask(taskName);
        }

        Runnable task = () -> {
            String currentTime = LocalDateTime.now().format(formatter);
            logger.info("[动态固定延迟任务-{}] 执行，当前时间：{}", taskName, currentTime);
        };

        try {
            // 使用scheduleWithFixedDelay方法
            // 第一个参数是任务，第二个参数是延迟时间
            ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(task, delayMilliseconds);

            runningTasks.put(taskName, future);

            logger.info("固定延迟任务 '{}' 添加成功", taskName);
            return true;
        } catch (Exception e) {
            logger.error("添加固定延迟任务 '{}' 失败：{}", taskName, e.getMessage());
            return false;
        }
    }

    /**
     * 动态添加固定频率任务
     *
     * 【注意事项】
     * 如果任务执行时间 > 指定的频率时间
     * 任务会连续执行（追赶模式）
     *
     * @param taskName 任务名称
     * @param intervalMilliseconds 间隔时间（毫秒）
     * @return 任务是否添加成功
     */
    public boolean addFixedRateTask(String taskName, long intervalMilliseconds) {
        logger.info("添加固定频率任务：{}，间隔：{}ms", taskName, intervalMilliseconds);

        if (runningTasks.containsKey(taskName)) {
            cancelTask(taskName);
        }

        Runnable task = () -> {
            String currentTime = LocalDateTime.now().format(formatter);
            logger.info("[动态固定频率任务-{}] 执行，当前时间：{}", taskName, currentTime);
        };

        try {
            // scheduleAtFixedRate: 固定频率执行
            ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, intervalMilliseconds);

            runningTasks.put(taskName, future);

            logger.info("固定频率任务 '{}' 添加成功", taskName);
            return true;
        } catch (Exception e) {
            logger.error("添加固定频率任务 '{}' 失败：{}", taskName, e.getMessage());
            return false;
        }
    }

    /**
     * 取消指定的任务
     *
     * 【取消任务的影响】
     * - 如果任务正在执行，会尝试中断它
     * - 如果任务已完成，取消操作没有效果
     * - 如果任务在等待队列中，会被移除
     *
     * @param taskName 任务名称
     * @return 任务是否取消成功
     */
    public boolean cancelTask(String taskName) {
        ScheduledFuture<?> future = runningTasks.get(taskName);
        if (future == null) {
            logger.warn("任务 '{}' 不存在", taskName);
            return false;
        }

        // cancel方法尝试取消任务
        // 参数true表示允许对正在执行的任务发送中断信号
        boolean cancelled = future.cancel(true);

        if (cancelled) {
            runningTasks.remove(taskName);
            logger.info("任务 '{}' 取消成功", taskName);
        } else {
            logger.warn("任务 '{}' 取消失败（可能已完成或在等待队列中）", taskName);
        }

        return cancelled;
    }

    /**
     * 暂停指定的任务
     *
     * 【暂停 vs 取消】
     * - 暂停：任务还在运行，但暂时不执行具体逻辑
     * - 取消：任务被终止，不会再执行
     *
     * 【实现方式】
     * 这里通过取消任务来实现"暂停"效果
     * 真正的暂停需要使用更复杂的信号量机制
     *
     * @param taskName 任务名称
     * @return 是否暂停成功
     */
    public boolean pauseTask(String taskName) {
        logger.info("暂停任务：{}", taskName);
        return cancelTask(taskName);
    }

    /**
     * 判断任务是否还在运行
     *
     * @param taskName 任务名称
     * @return 任务是否在运行
     */
    public boolean isTaskRunning(String taskName) {
        ScheduledFuture<?> future = runningTasks.get(taskName);
        return future != null && !future.isDone();
    }

    /**
     * 判断任务是否已完成（包含取消）
     *
     * @param taskName 任务名称
     * @return 任务是否完成
     */
    public boolean isTaskDone(String taskName) {
        ScheduledFuture<?> future = runningTasks.get(taskName);
        return future != null && future.isDone();
    }

    /**
     * 获取所有正在运行的任务名称
     *
     * @return 任务名称列表
     */
    public String getRunningTasksInfo() {
        StringBuilder sb = new StringBuilder("正在运行的任务：\n");
        runningTasks.forEach((name, future) -> {
            sb.append(String.format("  - %s: %s\n",
                    name,
                    future.isDone() ? "已完成" : "运行中"));
        });
        return sb.toString();
    }

    /**
     * 动态修改任务的执行频率
     *
     * 【实现思路】
     * 1. 取消旧任务
     * 2. 创建新任务（使用新的频率）
     *
     * 注意：这实际上是重新创建一个任务，而不是修改现有任务
     *
     * @param taskName 任务名称
     * @param newIntervalMilliseconds 新的间隔时间
     * @return 是否修改成功
     */
    public boolean updateTaskInterval(String taskName, long newIntervalMilliseconds) {
        logger.info("更新任务 '{}' 的间隔为：{}ms", taskName, newIntervalMilliseconds);

        // 获取旧任务的信息
        // 注意：这里简化了实现，实际项目中可能需要保存更多的任务配置信息
        ScheduledFuture<?> oldFuture = runningTasks.get(taskName);
        if (oldFuture == null) {
            logger.warn("任务 '{}' 不存在，无法更新", taskName);
            return false;
        }

        // 取消旧任务
        cancelTask(taskName);

        // 创建一个新任务，使用新的间隔
        String newTaskName = taskName + "-updated";
        return addFixedRateTask(newTaskName, newIntervalMilliseconds);
    }

    /**
     * 创建并调度一个一次性任务
     *
     * 【使用场景】
     * - 延迟执行某个操作
     * - 只在特定时间执行一次的任务
     * - 定时提醒
     *
     * @param taskName 任务名称
     * @param delayMilliseconds 延迟时间（毫秒）
     */
    public void scheduleOneTimeTask(String taskName, long delayMilliseconds) {
        logger.info("添加一次性任务：{}，延迟：{}ms", taskName, delayMilliseconds);

        Runnable task = () -> {
            String currentTime = LocalDateTime.now().format(formatter);
            logger.info("[一次性任务-{}] 执行，当前时间：{}", taskName, currentTime);
        };

        // schedule方法用于调度一次性任务
        // 任务会在指定的延迟后执行一次
        // 注意：TaskScheduler.schedule()不接受Duration，需要使用Instant
        Instant executionTime = Instant.now().plusMillis(delayMilliseconds);
        taskScheduler.schedule(task, executionTime);
    }
}
