package com.example;

import com.example.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * MainApp - Spring Framework 基础入门示例的主入口类
 *
 * ============================================
 * 本类的目的
 * ============================================
 * 这个类演示了 Spring Framework 最核心的功能：IoC（控制反转）。
 * 我们会通过对比"传统方式"和"Spring 方式"来展示：
 * 1. 传统方式创建对象的问题
 * 2. Spring IoC 容器如何解决这些问题
 *
 * ============================================
 * 什么是 IoC（控制反转）？
 * ============================================
 * IoC = Inversion of Control（控制反转）
 *
 * 想象一个场景：
 * - 传统方式：你自己去菜市场买菜、洗菜、切菜、炒菜（控制权在你手里）
 * - IoC 方式：你去餐厅点菜，厨师帮你做好一切（控制权交给了餐厅/框架）
 *
 * 在编程中：
 * - 传统方式：程序代码自己"new"创建对象，决定何时创建、如何销毁
 * - IoC 方式：把对象的创建和销毁交给 Spring 容器来管理，程序只需要"使用"对象
 *
 * 这样有什么好处？
 * 1. 松耦合：对象不需要知道它依赖的其他对象是如何创建的
 * 2. 易维护：修改一个类的实现，不影响使用它的其他类
 * 3. 易测试：可以很方便地替换成测试用的 mock 对象
 * 4. 易于管理：对象的生命周期由容器统一管理
 *
 * ============================================
 * 什么是 ApplicationContext？
 * ============================================
 * ApplicationContext 是 Spring IoC 容器的核心接口。
 * 把它想象成一个"对象的大管家"：
 * - 它知道如何创建对象
 * - 它知道对象之间的依赖关系
 * - 它负责保管这些对象，需要时提供给我们
 *
 * ClassPathXmlApplicationContext 是 ApplicationContext 的一个实现类，
 * 它从类路径（classpath）下的 XML 文件中读取配置信息。
 */
public class MainApp {

    /**
     * 程序的主入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Spring Framework 基础示例演示");
        System.out.println("========================================");
        System.out.println();

        // ============================================
        // 第一部分：传统方式创建对象（不使用 Spring）
        // ============================================
        demonstrateTraditionalApproach();

        System.out.println();

        // ============================================
        // 第二部分：使用 Spring IoC 容器获取 Bean
        // ============================================
        demonstrateSpringApproach();

        System.out.println();

        // ============================================
        // 第三部分：总结对比
        // ============================================
        summarizeComparison();
    }

    /**
     * 演示传统方式创建和使用对象
     *
     * 为什么要演示传统方式？
     * 只有理解了传统方式的问题，才能真正理解 Spring 的价值。
     */
    private static void demonstrateTraditionalApproach() {
        System.out.println("【第一部分】传统方式创建对象（不使用 Spring）");
        System.out.println("----------------------------------------");

        // 假设 UserService 需要依赖 User 对象
        // 在传统方式中，我们需要自己创建和管理这些依赖

        // 步骤 1：手动创建 User 对象
        // 问题 1：如果创建过程很复杂（比如需要设置很多属性），每个地方都要重复
        // 问题 2：如果 User 的构造函数变了，所有创建 User 的地方都要改
        User user1 = new User();
        user1.setId(1L);
        user1.setName("张三");
        user1.setEmail("zhangsan@example.com");

        System.out.println("使用传统方式创建了第一个用户：" + user1);

        // 步骤 2：再创建一个用户
        // 问题：如果我们要在不同地方创建多个 User 对象，代码会重复
        // 而且如果创建逻辑变了（比如要加一个 age 字段），所有地方都要改
        User user2 = new User(2L, "李四", "lisi@example.com");
        System.out.println("使用传统方式创建了第二个用户：" + user2);

        // 问题 3：如果 UserService 依赖 User，我们需要手动组装
        // 这叫"依赖自己管理"，而不是"被注入"
        String userInfo = "用户信息：id=" + user1.getId() +
                          ", name=" + user1.getName() +
                          ", email=" + user1.getEmail();
        System.out.println(userInfo);

        System.out.println();
        System.out.println("【传统方式的问题】");
        System.out.println("1. 对象太多了要自己 new，代码重复");
        System.out.println("2. 对象之间的依赖关系自己管理，容易出错");
        System.out.println("3. 要修改对象的创建方式，所有用到的地方都要改");
        System.out.println("4. 很难进行单元测试（因为对象是自己创建的，不好替换）");
    }

    /**
     * 演示使用 Spring IoC 容器获取 Bean
     *
     * 什么是 Bean？
     * Bean 是 Spring 容器管理的对象。
     * 在 Spring 中，任何被容器管理的对象都叫 Bean。
     *
     * Bean 什么时候创建？
     * 默认情况下，ApplicationContext 会在启动时创建所有 singleton（单例）Bean。
     * 对于 prototype（原型）Bean，则在每次请求时创建新的实例。
     */
    private static void demonstrateSpringApproach() {
        System.out.println("【第二部分】使用 Spring IoC 容器获取 Bean");
        System.out.println("----------------------------------------");

        /*
         * 什么是 Spring 配置文件？
         * 我们需要告诉 Spring：
         * - 有哪些对象需要管理
         * - 这些对象是如何创建的
         * - 对象之间有什么依赖关系
         *
         * 在 Spring 中，可以通过 XML、注解或 Java 配置来声明这些信息。
         * 在这个示例中，我们使用 XML 配置方式。
         *
         * 但是注意：为了简化演示，我们这里直接用代码方式创建容器和注册 Bean，
         * 而不是读取 XML 配置文件。这样更容易运行和理解。
         * 实际项目中，配置通常放在 XML 或注解中。
         */

        // 创建一个简单的 Spring 配置类来演示
        System.out.println("步骤 1: 创建 Spring IoC 容器...");

        // 这里我们使用 AnnotationConfigApplicationContext
        // 它是 ApplicationContext 的一个实现，可以从 @Configuration 配置类中读取 Bean 定义
        // 关于配置类，请参考 AppConfig.java
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("Spring IoC 容器创建成功！");
        System.out.println();

        // 步骤 2：从容器中获取 Bean
        // 注意：我们不再用 new 来创建对象，而是"请求"容器给我们一个对象
        System.out.println("步骤 2: 从容器中获取 Bean...");

        /*
         * getBean() 方法是 Spring 容器最常用的方法之一。
         * 它的作用是：根据 Bean 的名称（或类型），从容器中获取已创建好的对象。
         *
         * 这里 "user" 是在 AppConfig.java 中定义 Bean 时指定的名字。
         * Spring 会自动帮我们创建 User 对象，并返回给我们。
         */
        User userFromSpring = (User) context.getBean("user");

        System.out.println("从 Spring 容器获取的用户：" + userFromSpring);
        System.out.println();

        // 步骤 3：再获取一次，看看是否是同一个对象
        System.out.println("步骤 3: 验证 Spring 容器的 Bean 管理特性...");
        User anotherUser = (User) context.getBean("user");

        System.out.println("第二次获取的 Bean：" + anotherUser);
        System.out.println("两次获取的是否是同一个对象？" + (userFromSpring == anotherUser));
        System.out.println();

        System.out.println("【结论】");
        System.out.println("由于 User Bean 的作用域是 singleton（单例），");
        System.out.println("所以每次 getBean(\"user\") 都返回同一个对象实例。");
        System.out.println("这意味着 Spring 容器只创建了一个 User 对象，所有地方共用。");
        System.out.println("这种设计可以节省内存，也保证了数据一致性。");

        // 关闭容器（可选，但在实际项目中应该做）
        // 这里使用try-with-resources语法自动关闭
        if (context instanceof AutoCloseable) {
            try {
                ((AutoCloseable) context).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 总结传统方式和 Spring 方式的对比
     */
    private static void summarizeComparison() {
        System.out.println("========================================");
        System.out.println("【总结】传统方式 vs Spring 方式");
        System.out.println("========================================");
        System.out.println();
        System.out.println("┌─────────────┬──────────────────┬──────────────────┐");
        System.out.println("│   对比项     │    传统方式       │    Spring 方式    │");
        System.out.println("├─────────────┼──────────────────┼──────────────────┤");
        System.out.println("│ 创建对象     │ 手动 new          │ 容器自动创建      │");
        System.out.println("│ 依赖管理     │ 自己组装          │ 容器自动注入      │");
        System.out.println("│ 代码耦合度   │ 高耦合            │ 低耦合            │");
        System.out.println("│ 可维护性     │ 难维护            │ 易维护            │");
        System.out.println("│ 可测试性     │ 难测试            │ 易测试            │");
        System.out.println("│ 扩展性       │ 差                │ 好                │");
        System.out.println("└─────────────┴──────────────────┴──────────────────┘");
        System.out.println();
        System.out.println("【为什么需要 Spring？】");
        System.out.println("1. 控制反转（IoC）：把对象创建和依赖管理的控制权交给容器");
        System.out.println("2. 依赖注入（DI）：容器自动把依赖的对象注入到需要的地方");
        System.out.println("3. 面向切面编程（AOP）：方便地处理横切关注点，如日志、事务");
        System.out.println("4. 事务管理：统一管理数据库事务，不用每个方法都写try-catch");
        System.out.println("5. 丰富的生态系统：Spring Boot、Spring Data、Spring Security 等");
        System.out.println();
        System.out.println("恭喜你完成了 Spring Framework 的入门学习！");
        System.out.println("下一步建议学习：");
        System.out.println("- Spring Boot（更快速的 Spring 开发体验）");
        System.out.println("- 依赖注入的更多方式（构造函数注入、Setter 注入）");
        System.out.println("- Bean 的作用域（singleton、prototype 等）");
    }
}
