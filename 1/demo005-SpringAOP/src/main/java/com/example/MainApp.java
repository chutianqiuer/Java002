package com.example;

import com.example.model.User;
import com.example.aop.service.UserService;
import com.example.aop.service.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring AOP 演示主应用程序
 *
 * 本类演示Spring AOP的完整使用流程：
 * 1. 通过Spring配置文件创建ApplicationContext
 * 2. 从容器中获取被代理的Bean
 * 3. 调用Bean的方法，观察AOP通知的执行
 *
 * Spring AOP 工作原理：
 * 1. Spring容器启动时，扫描所有@Aspect标注的类
 * 2. 为每个切面创建代理对象（Proxy）
 * 3. 代理对象包含目标对象和切面逻辑
 * 4. 当调用代理对象的方法时：
 *    - 如果方法匹配切入点表达式
 *    - 则先执行切面的通知逻辑
 *    - 再执行目标方法
 *    - 最后执行通知的后置逻辑
 *
 * Spring AOP 代理机制：
 * 1. JDK动态代理：基于接口的代理，要求目标类实现接口
 * 2. CGLIB代理：基于类的代理，通过继承目标类创建代理
 * Spring AOP默认：
 * - 如果目标类有接口，使用JDK动态代理
 * - 如果目标类没有接口或配置为强制使用CGLIB，使用CGLIB代理
 *
 * 五种通知执行顺序（正常流程）：
 * 1. @Before 前置通知
 * 2. 目标方法执行
 * 3. @AfterReturning 返回通知（如果正常返回）
 * 4. @After 后置通知（无论是否异常）
 *
 * 五种通知执行顺序（异常流程）：
 * 1. @Before 前置通知
 * 2. 目标方法执行并抛出异常
 * 3. @AfterThrowing 异常通知
 * 4. @After 后置通知（无论是否异常）
 *
 * @Around 通知执行顺序（最完整）：
 * 1. @Around 前置逻辑
 * 2. proceed()调用目标方法
 * 3. @Around 返回逻辑 或 @Around 异常逻辑
 * 4. @Around 最终逻辑（finally）
 */
public class MainApp {

    /**
     * 主方法 - 程序入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("       Spring AOP 演示程序启动");
        System.out.println("=================================================\n");

        // ========== 第一步：创建Spring容器 ==========
        // ClassPathXmlApplicationContext 从类路径加载Spring配置文件
        // 配置文件路径：src/main/resources/applicationContext.xml
        System.out.println("【步骤1】创建Spring ApplicationContext容器...");
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println("【步骤1】完成：Spring容器创建成功！\n");

        // ========== 第二步：从容器获取Bean ==========
        // 注意：获取的是被Spring AOP代理过的对象
        // 而不是原始的UserService对象
        System.out.println("【步骤2】从容器获取AOP代理对象...");
        UserService userService = context.getBean("userService", UserService.class);
        OrderService orderService = context.getBean("orderService", OrderService.class);
        System.out.println("【步骤2】完成：获取到的UserService代理对象类：" + userService.getClass().getName());
        System.out.println("        获取到的OrderService代理对象类：" + orderService.getClass().getName());
        System.out.println("        （可以看到是$$开头，这是Spring AOP生成的代理类）\n");

        // ========== 第三步：调用业务方法，观察AOP效果 ==========
        System.out.println("=================================================");
        System.out.println("       开始演示各种AOP通知类型");
        System.out.println("=================================================\n");

        // ---------- 演示1：正常流程 ----------
        // 执行 findById 方法，观察 @Before、@AfterReturning、@After 通知
        System.out.println("【演示1】调用 findById 方法（正常流程）");
        System.out.println("预期：触发 @Before -> 目标方法 -> @AfterReturning -> @After");
        System.out.println("------------------------------------------------");
        try {
            User user = userService.findById(1L);
            System.out.println("返回的用户对象：" + user);
        } catch (Exception e) {
            System.out.println("发生异常：" + e.getMessage());
        }
        System.out.println();

        // ---------- 演示2：返回通知 ----------
        System.out.println("【演示2】调用 saveUser 方法（演示返回通知）");
        System.out.println("预期：触发 @Before -> 目标方法（正常返回）-> @AfterReturning -> @After");
        System.out.println("------------------------------------------------");
        try {
            User newUser = new User(2L, "newUser", "new@example.com");
            boolean result = userService.saveUser(newUser);
            System.out.println("保存结果：" + result);
        } catch (Exception e) {
            System.out.println("发生异常：" + e.getMessage());
        }
        System.out.println();

        // ---------- 演示3：更新操作（环绕通知） ----------
        System.out.println("【演示3】调用 updateUser 方法（演示@Around环绕通知）");
        System.out.println("预期：触发 @Around前置 -> 目标方法 -> @Around返回 -> @Around最终");
        System.out.println("------------------------------------------------");
        try {
            User updateUser = new User(1L, "updatedUser", "updated@example.com");
            boolean result = userService.updateUser(updateUser);
            System.out.println("更新结果：" + result);
        } catch (Exception e) {
            System.out.println("发生异常：" + e.getMessage());
        }
        System.out.println();

        // ---------- 演示4：异常流程 ----------
        System.out.println("【演示4】调用 deleteUser 方法（演示@AfterThrowing异常通知）");
        System.out.println("预期：触发 @Before -> 目标方法（抛异常）-> @AfterThrowing -> @After");
        System.out.println("------------------------------------------------");
        try {
            // 传入一个不存在的ID，会抛出RuntimeException
            userService.deleteUser(999L);
        } catch (Exception e) {
            System.out.println("主方法捕获到异常（这是正常的）：" + e.getMessage());
        }
        System.out.println();

        // ---------- 演示5：参数验证 ----------
        System.out.println("【演示5】调用 saveUser 方法（传入null参数，演示异常通知）");
        System.out.println("预期：触发参数验证异常通知");
        System.out.println("------------------------------------------------");
        try {
            userService.saveUser(null);
        } catch (Exception e) {
            System.out.println("主方法捕获到异常（这是正常的）：" + e.getMessage());
        }
        System.out.println();

        // ---------- 演示6：OrderService方法 ----------
        System.out.println("【演示6】调用 OrderService 的方法");
        System.out.println("预期：同样的切面会拦截OrderService的方法");
        System.out.println("------------------------------------------------");
        try {
            String orderId = orderService.createOrder(1L, "iPhone手机", 6999.00);
            System.out.println("创建的订单：" + orderId);
        } catch (Exception e) {
            System.out.println("发生异常：" + e.getMessage());
        }
        System.out.println();

        // ---------- 演示7：查询方法性能监控 ----------
        System.out.println("【演示7】调用 queryOrder 方法（演示查询方法性能监控）");
        System.out.println("预期：触发性能监控的@Around通知");
        System.out.println("------------------------------------------------");
        try {
            String orderInfo = orderService.queryOrder("ORDER123");
            System.out.println("查询结果：" + orderInfo);
        } catch (Exception e) {
            System.out.println("发生异常：" + e.getMessage());
        }
        System.out.println();

        // ---------- 演示8：方法拦截 ----------
        System.out.println("【演示8】调用 batchOperation 方法（演示方法拦截）");
        System.out.println("预期：@Around通知拦截方法，不执行目标方法");
        System.out.println("------------------------------------------------");
        try {
            String result = userService.batchOperation();
            System.out.println("方法返回：" + result);
            System.out.println("（注意：目标方法batchOperation()的打印语句没有输出，因为被@Around拦截了）");
        } catch (Exception e) {
            System.out.println("发生异常：" + e.getMessage());
        }
        System.out.println();

        // ---------- 演示9：取消订单（异常演示） ----------
        System.out.println("【演示9】调用 cancelOrder 方法（无效订单号，演示异常通知）");
        System.out.println("预期：触发业务异常通知");
        System.out.println("------------------------------------------------");
        try {
            orderService.cancelOrder("INVALID", "不想要了");
        } catch (Exception e) {
            System.out.println("主方法捕获到异常（这是正常的）：" + e.getMessage());
        }
        System.out.println();

        // ========== 第四步：关闭容器 ==========
        System.out.println("=================================================");
        System.out.println("       Spring AOP 演示程序结束");
        System.out.println("=================================================");

        // 关闭Spring容器，释放资源
        if (context instanceof ClassPathXmlApplicationContext) {
            ((ClassPathXmlApplicationContext) context).close();
        }
    }
}
