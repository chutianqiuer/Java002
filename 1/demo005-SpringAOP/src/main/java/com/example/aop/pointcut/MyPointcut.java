package com.example.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;

/**
 * 自定义切入点表达式类
 *
 * 为什么要单独定义切入点？
 * 1. 重用性：同一个切入点可能被多个通知使用
 * 2. 可维护性：修改切入点只需改一处
 * 3. 可读性：给切入点起有意义的名称
 * 4. 组合性：可以组合多个切入点
 *
 * 切入点表达式详解：
 *
 * 1. execution - 匹配方法执行的连接点
 *    语法：execution(返回类型 包名.类名.方法名(参数类型))
 *    示例：execution(* com.example.service.UserService.findById(Long))
 *    解释：匹配UserService中返回任意类型、名为findById、接收Long参数的方法
 *
 * 2. within - 匹配指定类型内的所有方法
 *    语法：within(类型)
 *    示例：within(com.example.service.*)
 *    解释：匹配service包下所有类的所有方法
 *
 * 3. this - 匹配代理对象是指定类型的连接点
 *    语法：this(类型)
 *    示例：this(com.example.service.UserService)
 *    解释：匹配代理对象是UserService类型的所有方法
 *
 * 4. target - 匹配目标对象是指定类型的连接点
 *    语法：target(类型)
 *    示例：target(com.example.service.UserService)
 *    解释：匹配目标对象是UserService类型的所有方法
 *
 * 5. args - 匹配参数类型匹配的方法
 *    语法：args(参数类型)
 *    示例：args(Long, String)
 *    解释：匹配接收Long和String两个参数的方法
 *
 * 6. @target - 匹配具有指定注解类型的目标对象
 *    语法：@target(注解类型)
 *    示例：@target(org.springframework.stereotype.Service)
 *    解释：匹配被@Service注解标注的类的所有方法
 *
 * 7. @within - 匹配具有指定注解类型的类中的方法
 *    语法：@within(注解类型)
 *    示例：@within(org.springframework.stereotype.Service)
 *    解释：匹配被@Service标注的类中的所有方法
 *
 * 8. @annotation - 匹配具有指定注解的方法
 *    语法：@annotation(注解类型)
 *    示例：@annotation(org.springframework.transaction.annotation.Transactional)
 *    解释：匹配被@Transactional标注的方法
 *
 * 切入点组合运算符：
 * - &&：并且（两个条件都满足）
 * - ||：或者（满足任一条件）
 * - !：非（取反）
 *
 * 切入点函数参数通配符：
 * - * ：匹配任意类型或名字
 * - .. ：在包名中表示任意子包；在参数中表示任意参数
 * - + ：匹配指定类型的子类
 */
public class MyPointcut {

    // ==================== 基本切入点定义 ====================

    /**
     * 用户服务切入点
     * 匹配com.example.aop.service.UserService类中的所有方法
     *
     * within()特点：
     * - 匹配指定类/包内的所有连接点
     * - 比execution()更简单，但精度较低
     * - within(com.example.aop.service.UserService) 匹配UserService的所有方法
     */
    @Pointcut("within(com.example.aop.service.UserService)")
    public void userServicePointcut() {
        // 匹配UserService类的所有方法
    }

    /**
     * 订单服务切入点
     * 匹配com.example.aop.service.OrderService类中的所有方法
     */
    @Pointcut("within(com.example.aop.service.OrderService)")
    public void orderServicePointcut() {
        // 匹配OrderService类的所有方法
    }

    /**
     * 所有业务服务切入点
     * 组合userServicePointcut和orderServicePointcut
     */
    @Pointcut("userServicePointcut() || orderServicePointcut()")
    public void allServicePointcut() {
        // 组合两个切入点
    }

    // ==================== 方法名模式切入点 ====================

    /**
     * 查询方法切入点
     * 匹配所有以query、find、get开头的方法
     *
     * * query*(..) 解释：
     * - 第一个*：匹配任意返回类型
     * - query*：匹配以query开头的方法名
     * - (..)：匹配任意参数
     */
    @Pointcut("execution(* com.example..query*(..)) || " +
              "execution(* com.example..find*(..)) || " +
              "execution(* com.example..get*(..))")
    public void queryMethodPointcut() {
        // 匹配查询相关方法
    }

    /**
     * 写操作切入点
     * 匹配所有以save、insert、update、delete、remove开头的方法
     */
    @Pointcut("execution(* com.example..save*(..)) || " +
              "execution(* com.example..insert*(..)) || " +
              "execution(* com.example..update*(..)) || " +
              "execution(* com.example..delete*(..)) || " +
              "execution(* com.example..remove*(..))")
    public void writeMethodPointcut() {
        // 匹配写操作方法
    }

    // ==================== 参数类型切入点 ====================

    /**
     * 接收Long类型参数的方法切入点
     *
     * args(Long) 解释：
     * - 匹配接收一个Long类型参数的方法
     * - 不仅是service包，而是整个项目中所有符合条件的方法
     */
    @Pointcut("args(Long)")
    public void longParamPointcut() {
        // 匹配接收Long参数的方法
    }

    /**
     * 接收User类型参数的方法切入点
     *
     * this() vs target() vs args()：
     * - this(UserService)：匹配代理对象是UserService类型的方法
     * - target(UserService)：匹配目标对象是UserService类型的方法
     * - args(User)：匹配接收User类型参数的方法
     */
    @Pointcut("args(com.example.model.User)")
    public void userParamPointcut() {
        // 匹配接收User类型参数的方法
    }

    /**
     * 接收两个参数且第一个是Long的方法切入点
     *
     * args(Long, ..) 解释：
     * - 第一个参数是Long类型
     * - ..表示任意数量和类型的其他参数
     */
    @Pointcut("args(Long, ..)")
    public void longFirstParamPointcut() {
        // 匹配第一个参数是Long的方法
    }

    // ==================== 注解切入点 ====================

    /**
     * 公共切入点：所有public方法
     *
     * execution(public * *..*(..)) 分解：
     * - public：匹配public方法
     * - 第一个*：匹配任意返回类型
     * - *..*：任意包下的任意类
     * - 第二个*：任意方法名
     * - (..)：任意参数
     */
    @Pointcut("execution(public * *..*(..))")
    public void publicMethodPointcut() {
        // 匹配所有public方法
    }

    /**
     * 返回类型为boolean的方法切入点
     */
    @Pointcut("execution(boolean *..*(..))")
    public void booleanReturnPointcut() {
        // 匹配返回boolean的方法
    }

    /**
     * 返回类型为String的方法切入点
     */
    @Pointcut("execution(String *..*(..))")
    public void stringReturnPointcut() {
        // 匹配返回String的方法
    }

    // ==================== 组合切入点 ====================

    /**
     * 组合切入点示例：查询操作中接收Long参数的方法
     *
     * &&：两个条件必须同时满足
     */
    @Pointcut("queryMethodPointcut() && longParamPointcut()")
    public void queryWithLongParamPointcut() {
        // 查询方法中接收Long参数的
    }

    /**
     * 组合切入点示例：用户服务的写操作
     */
    @Pointcut("userServicePointcut() && writeMethodPointcut()")
    public void userWritePointcut() {
        // UserService中的写操作方法
    }

    /**
     * 组合切入点示例：非查询操作（取反）
     */
    @Pointcut("allServicePointcut() && !queryMethodPointcut()")
    public void nonQueryServicePointcut() {
        // 所有service方法中非查询的
    }

    // ==================== 高级切入点 ====================

    /**
     * 嵌套类切入点示例
     *
     * UserService+ 解释：
     * - +符号表示UserService及其所有子类/实现类
     * - 用于匹配接口或父类的所有实现类的方法
     */
    @Pointcut("execution(* com.example.aop.service.UserService+.*(..))")
    public void userServiceWithSubclassPointcut() {
        // 匹配UserService及其子类的所有方法
    }

    /**
     * 异常切入点
     * 匹配抛出特定类型异常的方法（通过throws声明）
     * 注意：这个不常用，因为Spring AOP主要关注方法执行
     */
    // @Pointcut("execution(* *..*(..) throws java.io.IOException)")
    // public void ioExceptionPointcut() {
    //     匹配声明抛出IOException的方法
    // }

    /**
     * 短信服务切入点（用于演示其他服务）
     * 演示如何为不同业务模块定义切入点
     */
    @Pointcut("within(com.example.aop.service.SmsService)")
    public void smsServicePointcut() {
        // 短信服务切入点
    }
}
