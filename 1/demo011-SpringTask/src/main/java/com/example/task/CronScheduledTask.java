package com.example.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Cron表达式定时任务示例
 *
 * 【功能说明】
 * 本类演示如何使用Cron表达式来配置定时任务
 * Cron表达式是最灵活的时间调度配置方式
 *
 * 【Cron表达式详解】
 * Cron表达式是一种用于配置定时任务的字符串，由6-7个时间元素组成
 * 格式：[秒] [分] [时] [日] [月] [星期] [年(可选)]
 *
 * 【各字段含义】
 * | 字段 | 允许值       | 特殊字符        |
 * |------|-------------|----------------|
 * | 秒   | 0-59        | , - * /        |
 * | 分   | 0-59        | , - * /        |
 * | 时   | 0-23        | , - * /        |
 * | 日   | 1-31        | , - * / ? L W  |
 * | 月   | 1-12 或 JAN-DEC | , - * /     |
 * | 星期 | 1-7 或 SUN-SAT | , - * / ? L # |
 * | 年   | 1970-2099   | , - * /        |
 *
 * 【特殊字符含义】
 * 1. * (星号)
 *    - 表示"每"个时间单位
 *    - 例如：*在秒字段表示每秒，*在分字段表示每分钟
 *
 * 2. , (逗号)
 *    - 表示值的列表
 *    - 例如：1,3,5 表示1、3、5
 *
 * 3. - (横杠)
 *    - 表示范围
 *    - 例如：1-5 表示1到5
 *
 * 4. / (斜杠)
 *    - 表示起始时间触发，然后每隔固定时间触发
 *    - 例如：0/5 表示0秒开始，每5秒触发
 *    - 等价于 0,5,10,15,20,25,30,35,40,45,50,55
 *
 * 5. ? (问号)
 *    - 表示不确定的值
 *    - 只能用在日和星期字段
 *    - 用于避免两个字段冲突
 *    - 例如：日和星期不能同时指定值，用?来消除歧义
 *
 * 6. L (Last)
 *    - 表示最后
 *    - 日字段：表示月末最后一天
 *    - 星期字段：表示周六（SAT）
 *    - 例如：L表示每月最后一天，L-3表示每月倒数第3天
 *
 * 7. W (Weekday)
 *    - 表示最近的工作日
 *    - 例如：15W表示15号最近的工作日
 *
 * 8. # (井号)
 *    - 表示第几个星期几
 *    - 例如：6#3表示第3个周五
 *
 * 【示例解析】
 * 1. "0 0 * * * ?"     - 每小时的整点执行
 * 2. "0 0 8 * * ?"     - 每天早上8点执行
 * 3. "0 30 8 * * ?"    - 每天早上8点30分执行
 * 4. "0 0/5 * * * ?"   - 每5分钟执行一次
 * 5. "0 0 8-18 * * ?"  - 早上8点到下午6点每小时执行
 * 6. "0 0 8 ? * MON-FRI" - 工作日早上8点执行
 * 7. "0 30 8 L * ?"    - 每月最后一天早上8点30分执行
 */
@Component
public class CronScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(CronScheduledTask.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private int executionCount = 0;

    /**
     * 任务1：每30秒执行一次
     *
     * 【Cron表达式 "0/30 * * * * ?" 解析】
     * - 0: 起始值，从0秒开始
     * - /30: 每隔30秒
     * - * * * * ?: 每分钟、每小时、每天、每月（忽略星期）
     *
     * 【执行时机】
     * 00秒、30秒的时候会触发
     * 格式化为: 10:00:00, 10:00:30, 10:01:00, 10:01:30, ...
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void executeEvery30Seconds() {
        executionCount++;
        String currentTime = LocalDateTime.now().format(formatter);
        logger.info("[Cron任务-30秒] 第{}次执行，时间：{}，线程：{}",
                    executionCount, currentTime, Thread.currentThread().getName());
    }

    /**
     * 任务2：每分钟的第30秒执行
     *
     * 【Cron表达式 "30 * * * * ?" 解析】
     * - 30: 在第30秒
     * - * * * * ?: 每分钟、每小时、每天、每月（忽略星期）
     *
     * 【执行时机】
     * 10:00:30, 10:01:30, 10:02:30, ...
     */
    @Scheduled(cron = "30 * * * * ?")
    public void executeAtSecond30() {
        String currentTime = LocalDateTime.now().format(formatter);
        logger.info("[Cron任务-每分30秒] 执行时间：{}，线程：{}",
                    currentTime, Thread.currentThread().getName());
    }

    /**
     * 任务3：每天早上8点到18点，每小时整点执行
     *
     * 【Cron表达式 "0 0 8-18 * * ?" 解析】
     * - 0: 0秒
     * - 0: 0分
     * - 8-18: 8点到18点
     * - * * ?: 每天每月（忽略星期）
     *
     * 【执行时机】
     * 08:00:00, 09:00:00, 10:00:00, ..., 18:00:00
     */
    @Scheduled(cron = "0 0 8-18 * * ?")
    public void executeEveryHourInWorkTime() {
        String currentTime = LocalDateTime.now().format(formatter);
        logger.info("[Cron任务-工作时段每小时] 执行时间：{}，线程：{}",
                    currentTime, Thread.currentThread().getName());
    }

    /**
     * 任务4：工作日（周一到周五）早上8点30分执行
     *
     * 【Cron表达式 "0 30 8 ? * MON-FRI" 解析】
     * - 0: 0秒
     * - 30: 30分
     * - 8: 8点
     * - ?: 不指定日
     * - MON-FRI: 周一到周五
     *
     * 【执行时机】
     * 周一早上8:30, 周二早上8:30, ..., 周五早上8:30
     * 周六、周日不执行
     */
    @Scheduled(cron = "0 30 8 ? * MON-FRI")
    public void executeOnWeekdays() {
        String currentTime = LocalDateTime.now().format(formatter);
        String dayOfWeek = LocalDateTime.now().getDayOfWeek().toString();
        logger.info("[Cron任务-工作日] 执行时间：{} ({}), 线程：{}",
                    currentTime, dayOfWeek, Thread.currentThread().getName());
    }

    /**
     * 任务5：每月15号凌晨0点执行
     *
     * 【Cron表达式 "0 0 0 15 * ?" 解析】
     * - 0: 0秒
     * - 0: 0分
     * - 0: 0点
     * - 15: 15号
     * - *: 每月
     * - ?: 忽略星期
     *
     * 【执行时机】
     * 每月15号0点执行，如：2月15日0:00, 3月15日0:00, ...
     */
    @Scheduled(cron = "0 0 0 15 * ?")
    public void executeOn15thOfMonth() {
        String currentTime = LocalDateTime.now().format(formatter);
        logger.info("[Cron任务-每月15号] 执行时间：{}", currentTime);
    }

    /**
     * 任务6：每月最后一个工作日执行
     *
     * 【Cron表达式 "0 0 0 LW * ?" 解析】
     * - 0: 0秒
     * - 0: 0分
     * - 0: 0点
     * - LW: 月最后一天或最近的工作日
     * - *: 每月
     *
     * 【LW说明】
     * L: 月最后一天
     * W: 最近的工作日（周一到周五）
     * LW: 表示如果最后一天是周六，则取周五；如果是周日，则取周一
     */
    @Scheduled(cron = "0 0 0 LW * ?")
    public void executeOnLastWorkdayOfMonth() {
        String currentTime = LocalDateTime.now().format(formatter);
        logger.info("[Cron任务-每月最后工作日] 执行时间：{}", currentTime);
    }

    /**
     * 任务7：每周三和周五下午5点执行
     *
     * 【Cron表达式 "0 0 17 ? * WED,FRI" 解析】
     * - 0: 0秒
     * - 0: 0分
     * - 17: 17点（下午5点）
     * - ?: 忽略日
     * - WED,FRI: 周三和周五
     *
     * 【执行时机】
     * 周三17:00, 周五17:00
     */
    @Scheduled(cron = "0 0 17 ? * WED,FRI")
    public void executeOnWednesdayAndFriday() {
        String currentTime = LocalDateTime.now().format(formatter);
        String dayOfWeek = LocalDateTime.now().getDayOfWeek().toString();
        logger.info("[Cron任务-周三周五下午5点] 执行时间：{} ({})",
                    currentTime, dayOfWeek);
    }

    /**
     * 任务8：每年1月1日凌晨0点执行（元旦）
     *
     * 【Cron表达式 "0 0 0 1 1 ?" 解析】
     * - 0: 0秒
     * - 0: 0分
     * - 0: 0点
     * - 1: 1号
     * - 1: 1月
     * - ?: 忽略星期
     *
     * 【执行时机】
     * 每年1月1日0:00执行
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void executeOnNewYearsDay() {
        String currentTime = LocalDateTime.now().format(formatter);
        logger.info("[Cron任务-元旦] 执行时间：{}", currentTime);
    }

    /**
     * 任务9：每天上午9点到下午5点，每半小时执行一次
     *
     * 【Cron表达式 "0 0/30 9-17 * * ?" 解析】
     * - 0: 0秒
     * - 0/30: 从0开始，每30分钟
     * - 9-17: 9点到17点
     * - * * ?: 每天每月（忽略星期）
     *
     * 【执行时机】
     * 09:00:00, 09:30:00, 10:00:00, ..., 17:30:00
     */
    @Scheduled(cron = "0 0/30 9-17 * * ?")
    public void executeEveryHalfHourDuringWorkHours() {
        String currentTime = LocalDateTime.now().format(formatter);
        logger.info("[Cron任务-工作时段每半小时] 执行时间：{}", currentTime);
    }

    /**
     * 常用Cron表达式参考表
     *
     * | 表达式              | 含义                              |
     * |-------------------|----------------------------------|
     * | 0 0 * * * ?      | 每小时整点执行                      |
     * | 0 0/5 * * * ?    | 每5分钟执行一次                     |
     * | 0 0 8 * * ?      | 每天早上8点执行                     |
     * | 0 30 8 * * ?     | 每天早上8点30分执行                  |
     * | 0 0 8-18 * * ?   | 每天8点到18点整点执行                |
     * | 0 0 8 ? * MON-FRI| 工作日早上8点执行                    |
     * | 0 30 8 ? * MON-FRI| 工作日早上8点30分执行               |
     * | 0 0 0 15 * ?     | 每月15号凌晨0点执行                  |
     * | 0 0 0 LW * ?     | 每月最后工作日凌晨0点执行             |
     * | 0 0 0 1,15 * ?   | 每月1号和15号凌晨0点执行             |
     * | 0 0 12 * * ?     | 每天中午12点执行                    |
     * | 0 15 10 * * ?    | 每天上午10点15分执行                |
     * | 0/10 * * * * ?   | 每10秒执行一次                      |
     * | 0 0/1 * * * ?    | 每分钟执行一次                      |
     * | 0 0 0 * * ?      | 每天午夜执行                        |
     */
}
