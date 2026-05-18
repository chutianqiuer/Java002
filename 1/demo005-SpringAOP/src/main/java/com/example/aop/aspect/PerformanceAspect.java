package com.example.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 性能监控切面 - 演示@Around环绕通知
 *
 * @Around（环绕通知）是最强大的通知类型，它完全控制目标方法的执行。
 *
 * 与其他通知的区别：
 * 1. @Before/@After - 无法控制目标方法是否执行
 * 2. @AfterReturning/@AfterThrowing - 无法同时处理成功和异常情况
 * 3. @Around - 可以完全控制目标方法的执行，包括：
 *    - 在目标方法执行前做些什么
 *    - 决定是否执行目标方法（通过proceed()）
 *    - 在目标方法执行后做些什么
 *    - 修改目标方法的返回值
 *    - 捕获并处理异常
 *    - 甚至多次执行目标方法
 *
 * 环绕通知的执行流程：
 * try {
 *     // @Around 前置逻辑
 *     // proceed()调用 -> 执行目标方法
 *     // @Around 返回逻辑
 * } catch(Exception e) {
 *     // @Around 异常逻辑
 * } finally {
 *     // @Around 最终逻辑
 * }
 */
@Aspect
@Component
public class PerformanceAspect {

    /**
     * 切入点：匹配UserService和OrderService中所有公共方法
     *
     * 切入点表达式组合使用：
     * - execution(...)：匹配特定方法
     * - within(...)：匹配特定类型
     * 使用 || 连接多个匹配条件
     */
    @Pointcut("execution(* com.example.aop.service.UserService.*(..)) || " +
              "execution(* com.example.aop.service.OrderService.*(..))")
    public void performancePointcut() {
        // 命名切入点
    }

    /**
     * 切入点：专门匹配需要性能监控的查询方法
     *
     * 方法命名约定：所有以query、find、get开头的方法都会被监控
     */
    @Pointcut("execution(* com.example..query*(..)) || " +
              "execution(* com.example..find*(..)) || " +
              "execution(* com.example..get*(..))")
    public void queryMethodPointcut() {
        // 查询方法切入点
    }

    // ==================== 环绕通知 @Around ====================

    /**
     * 性能监控环绕通知
     *
     * ProceedingJoinPoint与JoinPoint的区别：
     * - JoinPoint：只能获取信息，不能控制方法执行
     * - ProceedingJoinPoint：有一个proceed()方法，可以控制是否执行目标方法
     *
     * @Around方法的返回值必须与目标方法一致
     * @Around方法必须接收ProceedingJoinPoint作为参数
     *
     * @param joinPoint 连接点
     * @return 目标方法的返回值
     * @throws Throwable 如果目标方法抛出异常
     */
    @Around("performancePointcut()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        // ========== 前置逻辑 ==========
        String methodName = joinPoint.getSignature().toLongString();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        long startTime = System.currentTimeMillis();

        System.out.println("========== 【环绕通知-前置】 ==========");
        System.out.println("类名：" + className);
        System.out.println("方法名：" + methodName);
        System.out.println("开始时间：" + startTime + "ms");
        System.out.println("参数列表：[暂时跳过，以避免重复输出]");
        System.out.println("======================================");

        Object result = null;
        long endTime = 0;
        long duration = 0;

        try {
            // ========== 执行目标方法 ==========
            // proceed()方法调用是真正执行目标方法的地方
            // 如果不调用proceed()，目标方法将不会被执行
            // 这可以实现：权限校验、缓存、等功能

            System.out.println("[PerformanceAspect] 调用目标方法...");
            result = joinPoint.proceed(); // 执行目标方法
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;

            // ========== 正常返回逻辑 ==========
            System.out.println("========== 【环绕通知-返回】 ==========");
            System.out.println("方法执行成功！");
            System.out.println("返回类型：" + (result != null ? result.getClass().getSimpleName() : "void"));
            System.out.println("返回结果：" + result);
            System.out.println("执行耗时：" + duration + "ms");
            System.out.println("======================================");

            // 性能警告：如果方法执行时间超过1秒
            if (duration > 1000) {
                System.out.println("⚠️ 警告：方法执行时间超过1秒，请关注性能！");
            }

            return result;

        } catch (Throwable ex) {
            // ========== 异常处理逻辑 ==========
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;

            System.out.println("========== 【环绕通知-异常】 ==========");
            System.out.println("方法执行抛出异常！");
            System.out.println("异常类型：" + ex.getClass().getName());
            System.out.println("异常信息：" + ex.getMessage());
            System.out.println("执行耗时：" + duration + "ms");
            System.out.println("======================================");

            // 可以选择：
            // 1. 重新抛出异常
            throw ex;
            // 2. 或者返回一个默认值
            // return null;
            // 3. 或者转换为业务异常
            // throw new BusinessException("方法执行失败", ex);
        } finally {
            // ========== 最终逻辑 ==========
            // 无论是否异常，都会执行
            // 类似于try-finally中的finally块

            System.out.println("========== 【环绕通知-最终】 ==========");
            System.out.println("finally块：执行清理工作");
            System.out.println("总执行时间：" + duration + "ms");
            System.out.println("======================================");
        }
    }

    /**
     * 专门针对查询方法的性能监控
     *
     * 这个通知用于演示：
     * 1. 如何针对特定方法进行专项监控
     * 2. 环绕通知如何与切入点表达式配合
     */
    @Around("queryMethodPointcut()")
    public Object monitorQueryPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.nanoTime(); // 使用纳秒精度

        System.out.println("[QueryPerformance] 开始监控查询方法：" + methodName);

        try {
            Object result = joinPoint.proceed();
            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;

            System.out.println("[QueryPerformance] 查询方法执行完成：" + methodName);
            System.out.println("[QueryPerformance] 执行耗时：" + durationMs + "ms");

            // 如果查询超过500ms，打印警告
            if (durationMs > 500) {
                System.out.println("[QueryPerformance] ⚠️ 查询较慢，请检查是否有性能问题");
            }

            return result;
        } catch (Throwable t) {
            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;

            System.out.println("[QueryPerformance] 查询方法执行失败：" + methodName);
            System.out.println("[QueryPerformance] 执行耗时：" + durationMs + "ms");
            System.out.println("[QueryPerformance] 异常：" + t.getMessage());

            throw t;
        }
    }

    /**
     * 演示：如何使用环绕通知实现方法拦截（不执行）
     *
     * 这个例子展示了可以通过不调用proceed()来阻止方法执行
     */
    @Around("execution(* com.example.aop.service.UserService.batchOperation())")
    public Object preventBatchOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("========== 【环绕通知-拦截演示】 ==========");
        System.out.println("检测到批量操作请求");
        System.out.println("方法：" + joinPoint.getSignature().getName());
        System.out.println("当前系统负载较高，拒绝执行批量操作");
        System.out.println("==========================================");

        // 注意：这里没有调用proceed()，所以目标方法不会被执行
        // 这展示了环绕通知的强大控制能力

        return "操作已被拦截：系统繁忙，请稍后再试";
    }
}
