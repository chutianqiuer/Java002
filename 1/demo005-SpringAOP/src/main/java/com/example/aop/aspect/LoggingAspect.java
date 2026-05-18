package com.example.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 日志切面 - 演示@Before、@After、@AfterReturning通知
 *
 * AOP通知类型详解：
 *
 * 1. @Before（前置通知）
 *    - 执行时机：目标方法执行之前
 *    - 特点：无法阻止目标方法执行（除非抛异常）
 *    - 典型应用：参数验证、日志记录、权限检查
 *
 * 2. @After（后置通知）
 *    - 执行时机：目标方法执行之后（无论是否抛出异常）
 *    - 特点：相当于try-finally中的finally块
 *    - 典型应用：资源释放、清理工作
 *
 * 3. @AfterReturning（返回通知）
 *    - 执行时机：目标方法正常返回之后
 *    - 特点：只在方法正常返回时执行，异常时不执行
 *    - 典型应用：结果日志记录、返回值处理
 *
 * 4. @AfterThrowing（异常通知）
 *    - 执行时机：目标方法抛出异常时
 *    - 特点：只在抛出异常时执行，正常返回时不执行
 *    - 典型应用：异常日志记录、补偿逻辑、降级处理
 *
 * 切入点表达式说明：
 * - execution()：最常用，匹配方法执行的连接点
 * - within()：匹配指定类型内的所有方法
 * - this()：匹配代理对象是指定类型的连接点
 * - target()：匹配目标对象是指定类型的连接点
 * - args()：匹配参数类型匹配的方法
 */
@Aspect     // 标识这是一个切面类，Spring AOP会自动检测并注册
@Component  // 让Spring容器管理这个Bean
public class LoggingAspect {

    /**
     * 切入点表达式详解：
     *
     * execution(
     *   修饰符返回类型
     *   类路径.方法名(参数类型)
     *   异常类型（可选）
     * )
     *
     * 分解：execution(* com.example.aop.service.*.*(..))
     * - 第一个 * ：匹配任意返回类型
     * - com.example.aop.service.* ：匹配service包下的任意类
     * - 第二个 * ：匹配任意方法名
     * - (..) ：匹配任意参数（0个或多个）
     *
     * 更多表达式示例：
     * - execution(* com.example..*.*(..))           匹配com.example包及子包下所有类
     * - execution(* *..find*(..))                    匹配任意包下以find开头的方法
     * - execution(* com.example.service.UserService+.*(..))  匹配UserService及其子类
     * - within(com.example.aop.service.*)            匹配service包下所有类
     * - args(Long)                                   匹配只接收一个Long参数的方法
     */

    /**
     * 定义切入点：匹配UserService中所有以find、save、delete、update开头的方法
     * 切入点命名：loggingPointcut - 给其他通知引用
     */
    @Pointcut("execution(* com.example.aop.service.UserService.find*(..)) || " +
              "execution(* com.example.aop.service.UserService.save*(..)) || " +
              "execution(* com.example.aop.service.UserService.delete*(..)) || " +
              "execution(* com.example.aop.service.UserService.update*(..))")
    public void loggingPointcut() {
        // 这是一个命名切入点，供其他通知引用
        // 切入点本身不包含任何逻辑，只是一个标识
    }

    /**
     * 定义切入点：匹配OrderService中所有方法
     * 展示within()表达式的使用
     *
     * within表达式：
     * - within(com.example.aop.service.OrderService)
     *   匹配OrderService类中的所有方法
     * - within(com.example.aop.service..*)
     *   匹配service包及子包下所有类的所有方法
     */
    @Pointcut("within(com.example.aop.service.OrderService)")
    public void orderServicePointcut() {
        // 命名切入点，用于匹配OrderService
    }

    // ==================== 前置通知 @Before ====================

    /**
     * 前置通知示例
     *
     * JoinPoint（连接点）参数说明：
     * - 每个通知方法都可以接收JoinPoint作为第一个参数
     * - JoinPoint包含了当前连接点的所有信息
     *
     * JoinPoint常用方法：
     * - getSignature()：获取目标方法签名
     * - getTarget()：获取目标对象
     * - getArgs()：获取目标方法参数
     * - toLongString()：获取方法的完整签名
     */
    @Before("loggingPointcut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        // 获取目标方法信息
        String methodName = joinPoint.getSignature().toLongString();
        Object[] args = joinPoint.getArgs();

        System.out.println("========== 【前置通知】 ==========");
        System.out.println("方法名：" + methodName);
        System.out.println("参数列表：" + Arrays.toString(args));
        System.out.println("目标对象：" + joinPoint.getTarget().getClass().getName());
        System.out.println("================================");

        // 实际应用场景：
        // 1. 权限验证：检查用户是否有权限执行该方法
        // if (!hasPermission(methodName)) {
        //     throw new SecurityException("没有权限执行方法：" + methodName);
        // }
        // 2. 参数验证
        // validateArgs(args);
        // 3. 日志记录
        // log.info("开始执行方法：{}", methodName);
    }

    // ==================== 后置通知 @After ====================

    /**
     * 后置通知示例
     *
     * @After特点：
     * - 无论目标方法是否正常返回，都会执行
     * - 类似于try-catch-finally中的finally块
     * - 常用于资源释放、清理工作
     *
     * 注意：@After无法访问目标方法的返回值
     *       如果需要访问返回值，使用@AfterReturning
     */
    @After("loggingPointcut()")
    public void afterAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();

        System.out.println("========== 【后置通知】 ==========");
        System.out.println("方法执行完成：" + methodName);
        System.out.println("无论是否异常，都会执行这里的代码！");
        System.out.println("通常用于：资源释放、清理工作、释放锁等");
        System.out.println("================================");

        // 实际应用场景：
        // 1. 释放数据库连接
        // 2. 关闭文件流
        // 3. 释放锁
        // 4. 清除ThreadLocal变量
    }

    // ==================== 返回通知 @AfterReturning ====================

    /**
     * 返回通知示例
     *
     * @AfterReturning特点：
     * - 只在目标方法正常返回时执行
     * - 通过returning属性指定返回值参数名
     * - 可以访问目标方法的返回值
     *
     * 参数说明：
     * - JoinPoint joinPoint：连接点信息
     * - Object result：目标方法的返回值，变量名要与returning="result"一致
     */
    @AfterReturning(pointcut = "loggingPointcut()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();

        System.out.println("========== 【返回通知】 ==========");
        System.out.println("方法名：" + methodName);
        System.out.println("返回值：" + result);
        System.out.println("（仅在方法正常返回时执行）");
        System.out.println("================================");

        // 实际应用场景：
        // 1. 记录成功日志
        // 2. 对返回值进行后处理（如脱敏）
        // 3. 审计日志
        // 4. 缓存结果
    }

    /**
     * 返回通知：专门处理OrderService的返回值
     *
     * 这里演示如何在通知中处理不同类型的返回值
     */
    @AfterReturning(pointcut = "orderServicePointcut()", returning = "result")
    public void afterReturningForOrder(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();

        System.out.println("========== 【OrderService返回通知】 ==========");
        System.out.println("OrderService方法正常返回");
        System.out.println("方法名：" + methodName);
        System.out.println("返回值类型：" + (result != null ? result.getClass().getName() : "null"));
        System.out.println("============================================");

        // 可以根据返回值类型进行不同处理
        if (result instanceof String) {
            // 处理字符串返回值
            String strResult = (String) result;
            System.out.println("返回的字符串长度：" + strResult.length());
        }
    }
}
