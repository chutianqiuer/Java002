package com.example.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 验证切面 - 演示@AfterThrowing异常通知
 *
 * @AfterThrowing（异常通知）特点：
 * 1. 只在目标方法抛出异常时执行
 * 2. 正常返回时不执行
 * 3. 可以访问抛出的异常对象
 * 4. 无法修改异常或阻止其传播
 *
 * 使用场景：
 * 1. 异常日志记录：记录异常的详细信息
 * 2. 异常转换：将技术异常转换为业务异常
 * 3. 补偿逻辑：当方法失败时执行补偿操作
 * 4. 监控告警：当出现异常时发送告警通知
 *
 * 注意：
 * - @AfterThrowing不能完全"处理"异常，只是"观察"异常
 * - 如果需要捕获并处理异常，使用@Around通知
 * - 异常通知后的代码不会影响异常的继续传播
 */
@Aspect
@Component
public class ValidationAspect {

    /**
     * 切入点：匹配service包下所有类的所有方法
     */
    @Pointcut("execution(* com.example.aop.service.*.*(..))")
    public void servicePointcut() {
        // 命名切入点
    }

    /**
     * 切入点：匹配所有业务服务类
     *
     * this() vs target()：
     * - this(UserService)：匹配代理对象是UserService类型的连接点
     * - target(UserService)：匹配目标对象是UserService类型的连接点
     *
     * 在Spring AOP中，由于使用代理模式：
     * - this()匹配的是代理对象（Spring AOP创建的代理）
     * - target()匹配的是真实目标对象
     * 通常两者效果相同，但this()可以在代理创建前匹配，target()只能在创建后匹配
     */
    @Pointcut("target(com.example.aop.service.UserService) || " +
             "target(com.example.aop.service.OrderService)")
    public void businessServicePointcut() {
        // 目标对象类型切入点
    }

    // ==================== 异常通知 @AfterThrowing ====================

    /**
     * 异常通知示例 - 通用异常处理
     *
     * @AfterThrowing参数说明：
     * - pointcut/value：切入点表达式或命名的切入点
     * - throwing：异常参数名，必须与方法的参数名一致
     *
     * 方法参数：
     * - JoinPoint joinPoint：连接点信息
     * - Throwable exception：捕获的异常对象
     *
     * 注意：异常参数类型决定了能捕获哪种异常
     * - Throwable：捕获所有异常
     * - Exception：捕获所有检查异常
     * - RuntimeException：只捕获非检查异常
     * - 具体异常类型：如IllegalArgumentException，只捕获该类型
     */
    @AfterThrowing(pointcut = "servicePointcut()", throwing = "exception")
    public void handleException(JoinPoint joinPoint, Throwable exception) {
        // 获取方法签名
        String methodName = joinPoint.getSignature().toLongString();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        System.out.println("========== 【异常通知】 ==========");
        System.out.println("检测到方法执行异常！");
        System.out.println("类名：" + className);
        System.out.println("方法名：" + methodName);
        System.out.println("参数列表：" + Arrays.toString(args));
        System.out.println("异常类型：" + exception.getClass().getName());
        System.out.println("异常消息：" + exception.getMessage());
        System.out.println("异常通知：记录日志、发送告警等");
        System.out.println("================================");

        // 实际应用场景：
        // 1. 发送告警通知（邮件、短信、钉钉等）
        // sendAlert("异常告警", exception);
        //
        // 2. 记录详细日志
        // log.error("方法执行异常", exception);
        //
        // 3. 异常数据上报
        // reportException(exception);
        //
        // 4. 降级处理
        // doFallback();
    }

    /**
     * 异常通知 - 针对特定异常类型的处理
     *
     * 这个通知专门处理IllegalArgumentException
     * 演示如何针对不同异常类型进行不同处理
     */
    @AfterThrowing(
        pointcut = "businessServicePointcut()",
        throwing = "illegalArgException"
    )
    public void handleIllegalArgumentException(
            JoinPoint joinPoint,
            IllegalArgumentException illegalArgException) {

        String methodName = joinPoint.getSignature().getName();

        System.out.println("========== 【参数验证异常】 ==========");
        System.out.println("方法：" + methodName);
        System.out.println("异常类型：IllegalArgumentException");
        System.out.println("异常消息：" + illegalArgException.getMessage());
        System.out.println("建议：检查传入参数是否合法");
        System.out.println("====================================");

        // 特定异常处理
        // 1. 记录参数验证失败的日志
        // log.warn("参数验证失败：{}", illegalArgException.getMessage());
        //
        // 2. 统计参数验证失败次数
        // metrics.increment("param.validation.fail");
        //
        // 3. 记录详细参数信息用于排查
        // log.info("方法参数：{}", Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * 异常通知 - 针对业务异常的专门处理
     *
     * 演示如何处理业务层面的异常（如业务规则违反）
     */
    @AfterThrowing(
        pointcut = "businessServicePointcut()",
        throwing = "businessException"
    )
    public void handleBusinessException(
            JoinPoint joinPoint,
            RuntimeException businessException) {

        String methodName = joinPoint.getSignature().getName();

        System.out.println("========== 【业务异常】 ==========");
        System.out.println("方法：" + methodName);
        System.out.println("业务异常：" + businessException.getMessage());
        System.out.println("可能原因：业务规则违反或数据状态错误");
        System.out.println("=================================");

        // 业务异常处理
        // 1. 记录业务异常日志
        // log.business("业务异常：{}", businessException.getMessage());
        //
        // 2. 触发业务补偿流程
        // compensation.process(joinPoint);
        //
        // 3. 通知相关人员
        // notify.business(businessException);
    }

    /**
     * 异常通知 - 针对所有异常的综合处理
     *
     * 这是最通用的异常通知，能捕获所有Throwable异常
     * 放在最后，作为兜底处理
     */
    @AfterThrowing(
        pointcut = "execution(* com.example..*(..))",
        throwing = "throwable"
    )
    public void handleAnyException(JoinPoint joinPoint, Throwable throwable) {

        // 避免重复处理（如果已经被上面的通知处理过）
        // 可以通过异常类型判断来避免

        String methodName = joinPoint.getSignature().toShortString();

        System.out.println("========== 【兜底异常处理】 ==========");
        System.out.println("未处理的异常被捕获！");
        System.out.println("方法：" + methodName);
        System.out.println("异常：" + throwable.getClass().getName());
        System.out.println("消息：" + throwable.getMessage());
        System.out.println("====================================");

        // 最终兜底处理
        // 1. 确保异常被记录
        // log.error("未处理的异常", throwable);
        //
        // 2. 发送紧急告警
        // alert.emergency("发现未处理异常", throwable);
    }
}
