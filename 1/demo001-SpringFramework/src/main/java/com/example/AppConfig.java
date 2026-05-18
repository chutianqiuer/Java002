package com.example;

import com.example.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AppConfig - Spring 配置类
 *
 * ============================================
 * 什么是 @Configuration 注解？
 * ============================================
 * @Configuration 是 Spring 提供的注解，标记在一个类上表示：
 * "这个类包含了 Spring 容器的配置信息"
 *
 * 简单理解：@Configuration 类就像是一份"菜单"，告诉 Spring 容器：
 * - 需要创建哪些对象（Bean）
 * - 每个对象如何创建
 * - 对象之间的依赖关系是什么
 *
 * ============================================
 * 什么是 @Bean 注解？
 * ============================================
 * @Bean 注解用在方法上，表示：
 * "这个方法的返回值应该被 Spring 容器管理为一个 Bean"
 *
 * Bean 的名称默认使用方法名，这里就是 "user"
 * 如果想指定其他名称，可以用 @Bean(name = "自定义名称")
 *
 * ============================================
 * 为什么使用配置类而不是 XML？
 * ============================================
 * 过去 Spring 使用 XML 文件来配置 Bean，例如：
 * <bean id="user" class="com.example.model.User">
 *     <property name="id" value="100"/>
 *     ...
 * </bean>
 *
 * 现在更推荐使用 @Configuration 配置类，因为：
 * 1. 类型安全：配置类就是 Java 代码，编译时就能检查错误
 * 2. 强大功能：可以使用 Java 的所有语法（循环、条件、异常处理等）
 * 3. 易于维护：不需要在 XML 和 Java 代码之间切换
 * 4. 重构友好：IDE 的重构功能对 Java 代码支持更好
 *
 * ============================================
 * 这个配置类做了什么？
 * ============================================
 * 我们在这里定义了一个 User 类型的 Bean。
 * 当 Spring 容器启动时，它会调用 user() 方法，
 * 方法返回的 User 对象会被放入容器中管理。
 *
 * 注意：这个 User 对象的属性值是我们在这里硬编码的。
 * 在实际应用中，这些值可能来自配置文件、数据库等。
 */
@Configuration
public class AppConfig {

    /**
     * 定义一个名为 "user" 的 Bean
     *
     * @Bean 注解的方法的返回值会被 Spring 容器管理
     * Bean 的名称默认就是方法名 "user"
     *
     * @return 一个由 Spring 容器管理的 User 对象
     *
     * 为什么返回值类型是 User 而不是 Object？
     * - 这样更明确，代码更清晰
     * - 调用 getBean(User.class) 时可以直接用类型获取
     */
    @Bean
    public User user() {
        // 创建一个新的 User 对象
        // 注意：这里我们自己 new 了对象，但这个对象后续由 Spring 管理
        // 这不是传统意义上的"自己创建对象"，而是在配置如何创建对象
        User user = new User();

        // 设置属性值
        // 这些值可以来自配置文件、环境变量等，这里简化为硬编码
        user.setId(100L);
        user.setName("Spring管理的User");
        user.setEmail("spring@example.com");

        // 返回对象，Spring 会将这个对象纳入管理
        return user;
    }

    /*
     * 如果想创建多个 User Bean 实例，该怎么办？
     *
     * 方式 1：使用方法名区分
     * @Bean
     * public User user1() { ... }
     * @Bean
     * public User user2() { ... }
     *
     * 方式 2：使用 name 属性指定名称
     * @Bean(name = {"user1", "firstUser"})
     * public User user() { ... }
     *
     * 获取时使用：
     * context.getBean("user1", User.class);
     */
}
