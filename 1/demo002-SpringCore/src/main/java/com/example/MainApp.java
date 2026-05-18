/*
 * =====================================================
 * Spring Core IoC容器深入学习示例
 * =====================================================
 *
 * 【IoC（控制反转）简介】
 *
 * IoC（Inversion of Control，控制反转）是一种软件设计原则，
 * 旨在降低对象之间的耦合度。
 *
 * 传统方式：
 *   - 对象自己负责创建和管理它的依赖对象
 *   - 例如：new A() → A对象主动创建B对象
 *   - 问题：高度耦合，难以测试，依赖关系硬编码
 *
 * IoC方式：
 *   - 对象的创建和依赖管理交给外部容器（Spring IoC容器）
 *   - 对象被动等待容器注入依赖
 *   - 好处：松耦合，易于测试，可复用
 *
 * 【DI（依赖注入）简介】
 *
 * DI（Dependency Injection，依赖注入）是IoC的一种具体实现方式。
 * 它指的是通过构造函数、setter方法或字段将依赖对象注入到目标对象中。
 *
 * 依赖注入的两种主要方式：
 *   1. 构造器注入（Constructor Injection）
 *      - 通过构造函数传递依赖
 *      - 优点：依赖不可变，强制要求所有依赖在创建时提供
 *   2. Setter注入（Setter Injection）
 *      - 通过setter方法传递依赖
 *      - 优点：可选依赖，灵活可变
 *
 * 【IoC vs DI 的区别】
 *
 * IoC是一个更广泛的概念，指的是控制权的反转；
 * DI是实现IoC的一种具体方式。
 *
 * 简单理解：
 *   - IoC是一种思想/原则
 *   - DI是这种思想的具体实现
 *   - Spring中的IoC容器（如ApplicationContext）负责DI
 *
 * =====================================================
 */
package com.example;

import com.example.config.AppConfig;
import com.example.di.Consumer;
import com.example.ioc.CoffeeMaker;
import com.example.ioc.TeaMaker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 主应用程序入口
 *
 * 本类演示Spring IoC容器的使用，包括：
 * 1. 传统方式的耦合问题
 * 2. Spring IoC容器如何解决问题
 * 3. 依赖注入的两种方式
 */
public class MainApp {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Spring Core IoC容器深入学习示例");
        System.out.println("========================================\n");

        // ========================================
        // 第一部分：演示传统方式的耦合问题
        // ========================================
        System.out.println("【第一部分】传统方式的耦合问题演示");
        System.out.println("----------------------------------------");

        // 传统方式：对象自己创建依赖
        // 问题：TeaMaker直接在自己的代码中创建了Water（具体实现）
        // 这导致了高度的耦合，TeaMaker和Water的具体实现强绑定
        TeaMaker teaMaker = new TeaMaker();
        teaMaker.makeTea();

        System.out.println();

        // 同样的问题也存在于CoffeeMaker
        CoffeeMaker coffeeMaker = new CoffeeMaker();
        coffeeMaker.makeCoffee();

        /*
         * 传统方式的问题分析：
         *
         * 1. 耦合性高：TeaMaker直接new Water()，如果Water的构造函数变化，
         *    或者需要使用不同的Water实现，都需要修改TeaMaker的代码
         *
         * 2. 难以测试：如果要测试TeaMaker的逻辑，很难mock Water对象
         *
         * 3. 依赖关系不清晰：阅读TeaMaker代码不容易知道它依赖什么
         *
         * 4. 对象管理混乱：每次new都创建新对象，无法控制对象的生命周期
         */

        System.out.println("\n========================================\n");

        // ========================================
        // 第二部分：使用Spring IoC容器
        // ========================================
        System.out.println("【第二部分】Spring IoC容器演示");
        System.out.println("----------------------------------------");

        // 创建Spring IoC容器，使用注解配置类
        // AnnotationConfigApplicationContext是ApplicationContext的实现类
        // 它专门用于处理Java注解配置
        ApplicationContext context =
            new AnnotationConfigApplicationContext(AppConfig.class);

        /*
         * Spring容器创建过程：
         * 1. 扫描AppConfig中标注的@Bean方法
         * 2. 执行这些方法，创建bean实例
         * 3. 注册bean到容器中（使用方法名作为bean的默认名称）
         * 4. 解析bean之间的依赖关系，进行依赖注入
         */

        System.out.println("Spring IoC容器创建成功！\n");

        // ========================================
        // 第三部分：从容器获取Bean
        // ========================================
        System.out.println("【第三部分】从容器获取Bean");
        System.out.println("----------------------------------------");

        // 方式1：通过类型获取Bean（推荐方式）
        // Spring容器中同类型的Bean只能有一个，否则会抛出异常
        Consumer constructorInjectionConsumer = context.getBean(Consumer.class);
        System.out.println("通过类型获取Bean（构造器注入）: " + constructorInjectionConsumer);
        constructorInjectionConsumer.processMessage("Hello Spring IoC!", "email");

        System.out.println();

        // 方式2：通过名称获取Bean
        // @Bean方法名就是bean的名称，如"setterInjectionConsumer"
        Consumer setterInjectionConsumer = (Consumer) context.getBean("setterInjectionConsumer");
        System.out.println("通过名称获取Bean（Setter注入）: " + setterInjectionConsumer);
        setterInjectionConsumer.processMessage("Hello Spring IoC!", "sms");

        /*
         * Bean的命名规则：
         * - 默认情况下，@Bean方法名就是bean的名称
         * - 可以使用@Bean(name="自定义名称")指定多个名称
         * - 建议使用有意义的名称，如驼峰命名法
         */

        System.out.println("\n========================================\n");

        // ========================================
        // 第四部分：演示单例作用域（默认行为）
        // ========================================
        System.out.println("【第四部分】Bean作用域演示");
        System.out.println("----------------------------------------");

        // 默认情况下，Spring容器中的Bean是单例的
        // 多次获取同一个Bean，返回的是同一个实例
        Consumer consumer1 = context.getBean(Consumer.class);
        Consumer consumer2 = context.getBean(Consumer.class);

        System.out.println("Consumer 1 hashCode: " + consumer1.hashCode());
        System.out.println("Consumer 2 hashCode: " + consumer2.hashCode());
        System.out.println("是否是同一个实例: " + (consumer1 == consumer2));

        /*
         * Bean的作用域（Scope）：
         *
         * 1. singleton（默认）：单例模式，整个容器中只有一个实例
         *    - 优点：减少对象创建开销
         *    - 适用：无状态的Bean
         *
         * 2. prototype：每次获取都创建新实例
         *    - 优点：每次获取都是全新的对象
         *    - 适用：有状态的Bean
         *
         * 3. request：每个HTTP请求创建一个实例（Web应用）
         * 4. session：每个HTTP会话创建一个实例（Web应用）
         * 5. application：每个ServletContext创建一个实例（Web应用）
         * 6. websocket：每个WebSocket会话创建一个实例
         */

        System.out.println("\n========================================\n");

        // ========================================
        // 第五部分：关闭容器
        // ========================================
        System.out.println("【第五部分】关闭Spring容器");
        System.out.println("----------------------------------------");

        // 关闭IoC容器，释放资源
        //AnnotationConfigApplicationContext实现了AutoCloseable接口
        if (context instanceof AnnotationConfigApplicationContext) {
            ((AnnotationConfigApplicationContext) context).close();
        }

        System.out.println("Spring IoC容器已关闭，资源已释放。");

        System.out.println("\n========================================");
        System.out.println("示例运行完成！");
        System.out.println("========================================");
    }
}
