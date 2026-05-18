package com.example.async;

import com.example.model.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 异步服务类
 *
 * 【功能说明】
 * 本类演示Spring @Async注解的各种使用方式
 *
 * 【核心概念】
 *
 * 1. @Async注解
 *    - 标注在方法上，使方法异步执行
 *    - 调用者立即返回，不阻塞
 *    - 方法在独立线程中执行
 *
 * 2. 异步方法的返回值
 *    - void: 不需要返回结果
 *    - Future<T>: 早期版本使用，可以获取异步执行结果
 *    - CompletableFuture<T>: Java 8引入，更强大的异步编程
 *
 * 3. @Async的限制
 *    - 必须是public方法（代理限制）
 *    - 不能是static方法
 *    - 不能是final方法
 *    - 不能同类内部调用（失效）
 *    - 必须通过代理对象调用
 *
 * 【@Async失效的场景】
 *
 * 1. 同类内部调用
 *    ```java
 *    @Service
 *    public class MyService {
 *        public void outer() {
 *            inner();  // 不会异步执行！
 *        }
 *        @Async
 *        public void inner() {
 *            // 同步执行
 *        }
 *    }
 *    ```
 *    原因：outer调用inner是this调用，不经过Spring代理
 *
 * 2. 解决方案
 *    - 注入自身：@Autowired private MyService self;
 *    - 使用ApplicationContext获取Bean
 *    - 重新注入到新类中调用
 *
 * 【Future vs CompletableFuture】
 *
 * Future（Java 5引入）：
 * - get(): 阻塞获取结果
 * - cancel(): 取消任务
 * - isDone(): 判断是否完成
 * - 局限性：不能手动完成，不能组合多个Future
 *
 * CompletableFuture（Java 8引入）：
 * - 更灵活的异步编程
 * - 支持回调函数
 * - 支持组合多个异步操作
 * - 支持手动完成
 *
 * 【使用场景建议】
 * - 简单异步任务：@Async void method()
 * - 需要返回结果：@Async Future<T> method()
 * - 复杂异步流程：@Async CompletableFuture<T> method()
 */
@Service  // Spring服务组件
public class AsyncService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    /**
     * 示例1：基本异步方法（无返回值）
     *
     * 【@Async工作原理】
     * 1. Spring为AsyncService创建代理对象
     * 2. 当调用asyncTaskNoReturn()时，实际调用的是代理对象
     * 3. 代理对象将任务封装并提交到线程池
     * 4. 立即返回，任务在后台线程中执行
     *
     * 【注意事项】
     * - 方法必须是public（代理限制）
     * - 不能通过同类内部调用（this.asyncTaskNoReturn()会失效）
     */
    @Async  // 标记为异步方法
    public void asyncTaskNoReturn() {
        String threadName = Thread.currentThread().getName();
        logger.info("[异步任务-无返回值] 开始执行，线程：{}", threadName);

        // 模拟耗时操作
        try {
            Thread.sleep(3000);  // 模拟3秒的处理时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("任务被中断", e);
        }

        logger.info("[异步任务-无返回值] 执行完成，线程：{}", threadName);
    }

    /**
     * 示例2：带返回值的异步方法（返回Future）
     *
     * 【Future接口】
     * - 代表异步计算的结果
     * - 可以检查任务是否完成、获取结果、取消任务
     *
     * 【主要方法】
     * - get(): 阻塞等待结果返回
     * - get(long timeout, TimeUnit unit): 等待指定时间
     * - isDone(): 检查任务是否完成
     * - isCancelled(): 检查任务是否被取消
     * - cancel(boolean mayInterruptIfRunning): 取消任务
     *
     * 【使用场景】
     * - 需要在后续代码中获取异步任务的结果
     * - 需要知道任务是否完成
     *
     * @param taskName 任务名称
     * @return Future<String> 异步任务的结果
     */
    @Async
    public Future<String> asyncTaskWithFutureReturn(String taskName) {
        String threadName = Thread.currentThread().getName();
        logger.info("[异步任务-Future] '{}' 开始执行，线程：{}", taskName, threadName);

        try {
            // 模拟耗时操作
            Thread.sleep(2000);

            String result = taskName + " - 执行完成，结果：" + System.currentTimeMillis();

            logger.info("[异步任务-Future] '{}' 执行完成，线程：{}，结果：{}",
                        taskName, threadName, result);

            // 返回AsyncResult，它实现了Future接口
            return new org.springframework.scheduling.annotation.AsyncResult<>(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("[异步任务-Future] '{}' 执行被中断", taskName, e);
            // 返回一个已完成但带有异常信息的Future
            return new org.springframework.scheduling.annotation.AsyncResult<>("任务被中断");
        }
    }

    /**
     * 示例3：带返回值的异步方法（返回CompletableFuture）
     *
     * 【CompletableFuture优势】
     * - 更强大的异步编程能力
     * - 支持回调函数
     * - 支持组合多个异步操作
     * - 支持手动完成
     *
     * 【常用方法】
     * - thenApply(): 转换结果
     * - thenAccept(): 消费结果
     * - thenCompose(): 组合另一个CompletableFuture
     * - exceptionally(): 异常处理
     * - allOf(): 等待所有CompletableFuture完成
     *
     * @param taskName 任务名称
     * @return CompletableFuture<String> 异步任务的结果
     */
    @Async
    public CompletableFuture<String> asyncTaskWithCompletableFuture(String taskName) {
        String threadName = Thread.currentThread().getName();
        logger.info("[异步任务-CompletableFuture] '{}' 开始执行，线程：{}", taskName, threadName);

        try {
            // 模拟耗时操作
            Thread.sleep(2000);

            String result = taskName + " - CompletableFuture执行完成";

            logger.info("[异步任务-CompletableFuture] '{}' 执行完成，线程：{}",
                        taskName, threadName);

            // 使用CompletableFuture.completedFuture()创建已完成的Future
            return CompletableFuture.completedFuture(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("[异步任务-CompletableFuture] '{}' 执行被中断", taskName, e);
            // 创建异常完成的Future
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 示例4：返回自定义TaskResult的异步方法
     *
     * 【使用场景】
     * - 需要返回更丰富的任务状态信息
     * - 需要返回执行时间、错误信息等
     *
     * @param taskName 任务名称
     * @return TaskResult<String> 任务执行结果
     */
    @Async
    public TaskResult<String> asyncTaskWithTaskResult(String taskName) {
        String threadName = Thread.currentThread().getName();
        logger.info("[异步任务-TaskResult] '{}' 开始执行，线程：{}", taskName, threadName);

        long startTime = System.currentTimeMillis();

        try {
            // 模拟耗时操作
            Thread.sleep(2000);

            long executionTime = System.currentTimeMillis() - startTime;
            String result = taskName + " 执行完成，耗时：" + executionTime + "ms";

            logger.info("[异步任务-TaskResult] '{}' {}", taskName, result);

            return TaskResult.success(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("[异步任务-TaskResult] '{}' 执行被中断，耗时：{}ms",
                        taskName, executionTime, e);
            return TaskResult.failure("任务被中断：" + e.getMessage());
        }
    }

    /**
     * 示例5：演示@Async的同类调用失效问题
     *
     * 【问题说明】
     * 在同一个类中，一个方法调用另一个标注@Async的方法
     * 会导致@Async失效，变成同步执行
     *
     * 【原因分析】
     * - Spring的@Async是基于代理实现的
     * - 同类内部调用是this调用，不经过代理
     * - 因此@Async注解不会生效
     *
     * 【解决方案】
     * 1. 注入自身，通过代理对象调用
     * 2. 使用ApplicationContext获取Bean
     * 3. 将异步方法移到另一个Service中
     */

    // 注意：这里不能直接注入AsyncService自己（会递归）
    // 需要使用特殊的注入方式，参见下面的correctCall()方法

    /**
     * 【错误示例】同类内部调用
     * 这个方法不会异步执行
     */
    public void wrongCall() {
        logger.info("[错误示例] 开始调用asyncTaskNoReturn");
        // 这里调用的是this，所以不会走代理
        // @Async注解不会生效，会同步执行
        asyncTaskNoReturn();
        logger.info("[错误示例] asyncTaskNoReturn调用完成（实际是同步执行的）");
    }

    /**
     * 【正确示例】通过注入自身调用
     * 需要在配置类中特殊处理，或使用ApplicationContext
     */

    @Autowired
    private AsyncService self;  // 注入自身（通过代理）

    /**
     * 【正确示例】通过代理对象调用
     * 使用注入的self来调用，确保走代理
     */
    public void correctCall() {
        logger.info("[正确示例] 开始通过代理调用asyncTaskNoReturn");
        // 通过注入的self调用，会走Spring代理
        // @Async注解会生效，异步执行
        self.asyncTaskNoReturn();
        logger.info("[正确示例] asyncTaskNoReturn调用完成（实际是异步执行的）");
    }

    /**
     * 示例6：带参数的异步方法
     *
     * @param message 消息内容
     * @param times 执行次数
     */
    @Async
    public void asyncTaskWithParams(String message, int times) {
        String threadName = Thread.currentThread().getName();
        logger.info("[异步任务-带参数] 开始执行，消息：{}，次数：{}，线程：{}",
                    message, times, threadName);

        for (int i = 0; i < times; i++) {
            logger.info("[异步任务-带参数] 第{}/{}次执行：{}", i + 1, times, message);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("[异步任务-带参数] 执行完成，线程：{}", threadName);
    }

    /**
     * 示例7：带异常处理的异步方法
     *
     * @param taskName 任务名称
     * @param shouldFail 是否模拟失败
     * @return 任务结果
     */
    @Async
    public TaskResult<String> asyncTaskWithException(String taskName, boolean shouldFail) {
        String threadName = Thread.currentThread().getName();
        logger.info("[异步任务-异常处理] '{}' 开始执行，线程：{}", taskName, threadName);

        try {
            Thread.sleep(1000);

            if (shouldFail) {
                throw new RuntimeException("模拟任务执行失败！");
            }

            return TaskResult.success(taskName + " 执行成功");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return TaskResult.failure("任务被中断");
        } catch (Exception e) {
            logger.error("[异步任务-异常处理] '{}' 执行异常", taskName, e);
            return TaskResult.failure("任务执行失败：" + e.getMessage());
        }
    }

    /**
     * 示例8：组合多个异步任务（使用CompletableFuture）
     *
     * 【使用场景】
     * - 需要同时执行多个异步任务
     * - 等待所有任务完成后处理结果
     *
     * @param task1Name 任务1名称
     * @param task2Name 任务2名称
     * @return 组合后的结果
     */
    public CompletableFuture<String> combinedAsyncTasks(String task1Name, String task2Name) {
        logger.info("[组合任务] 开始执行，任务1：{}，任务2：{}", task1Name, task2Name);

        // 同时执行两个异步任务
        CompletableFuture<String> task1 = asyncTaskWithCompletableFuture(task1Name);
        CompletableFuture<String> task2 = asyncTaskWithCompletableFuture(task2Name);

        // 等待所有任务完成
        return task1.thenCombine(task2, (result1, result2) -> {
            // 两个任务都完成后执行这里
            logger.info("[组合任务] 任务1结果：{}", result1);
            logger.info("[组合任务] 任务2结果：{}", result2);
            return "组合结果：" + result1 + " | " + result2;
        });
    }
}
