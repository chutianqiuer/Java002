package com.example;

import com.example.context.*;
import com.example.env.EnvironmentDemo;
import com.example.env.ProfileDemo;
import com.example.resource.ResourceLoaderDemo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * MainApp - Spring Context 主入口类
 *
 * 本类是整个演示项目的入口点，展示了Spring ApplicationContext的多种实现和用法
 *
 * 【ApplicationContext 简介】
 * ApplicationContext是Spring容器的高级接口，继承自BeanFactory接口
 * 与BeanFactory相比，ApplicationContext提供了：
 * 1. 更多的配置方式（XML、注解、Java配置）
 * 2. 事件发布机制
 * 3. 国际化支持
 * 4. 自动Bean后处理器注册
 * 5. 更多的容器扩展点
 *
 * 【常见的ApplicationContext实现类】
 * 1. ClassPathXmlApplicationContext - 从classpath加载XML配置
 * 2. FileSystemXmlApplicationContext - 从文件系统加载XML配置
 * 3. AnnotationConfigApplicationContext - 从注解配置类加载
 * 4. XmlWebApplicationContext - Web应用的XML配置
 * 5. AnnotationConfigWebApplicationContext - Web应用的注解配置
 * 6. GenericApplicationContext - 通用容器，支持多种配置方式
 * 7. GenericXmlApplicationContext - 通用XML容器
 *
 * 【适用场景】
 * - ClassPathXmlApplicationContext: 适合从jar包或classpath中加载配置文件的场景
 * - FileSystemXmlApplicationContext: 适合需要灵活指定配置文件路径的场景
 * - AnnotationConfigApplicationContext: 适合使用注解和Java配置类的场景
 * - Web应用容器由Web服务器初始化，自动创建相应的WebApplicationContext
 */
public class MainApp {

    /**
     * 程序主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Spring Context 深入学习演示");
        System.out.println("========================================");
        System.out.println();

        try {
            // ==================== 第一部分：演示ClassPathXmlApplicationContext ====================
            // 适用场景：从classpath（如src/main/resources目录）中加载XML配置文件
            // 优点：配置简单，配置文件随jar包一起部署，适合类加载器加载的资源
            System.out.println("【第一部分】ClassPathXmlApplicationContext 示例");
            System.out.println("-".repeat(50));
            ClassPathXmlApplicationContextDemo.demo();
            System.out.println();

            // ==================== 第二部分：演示FileSystemXmlApplicationContext ====================
            // 适用场景：从文件系统路径加载XML配置文件
            // 优点：配置文件可以放在任意位置，适合需要外部化配置的场景
            System.out.println("【第二部分】FileSystemXmlApplicationContext 示例");
            System.out.println("-".repeat(50));
            FileSystemXmlApplicationContextDemo.demo();
            System.out.println();

            // ==================== 第三部分：演示AnnotationConfigApplicationContext ====================
            // 适用场景：使用注解和Java配置类的方式配置Spring容器
            // 优点：类型安全，支持复杂配置，适合现代化的Spring应用
            System.out.println("【第三部分】AnnotationConfigApplicationContext 示例");
            System.out.println("-".repeat(50));
            AnnotationConfigApplicationContextDemo.demo();
            System.out.println();

            // ==================== 第四部分：演示WebApplicationContext ====================
            // 说明：WebApplicationContext不能独立创建，这里提供说明和示例代码
            System.out.println("【第四部分】WebApplicationContext 说明");
            System.out.println("-".repeat(50));
            WebApplicationContextDemo.explain();
            System.out.println();

            // ==================== 第五部分：演示Environment接口 ====================
            // Environment接口用于访问应用程序环境和配置属性
            // 支持获取profiles、properties等配置信息
            System.out.println("【第五部分】Environment 接口示例");
            System.out.println("-".repeat(50));
            EnvironmentDemo.demo();
            System.out.println();

            // ==================== 第六部分：演示@Profile注解 ====================
            // @Profile注解用于在不同环境下激活不同的Bean
            // 例如：开发环境、测试环境、生产环境可以使用不同的数据库配置
            System.out.println("【第六部分】@Profile 注解示例");
            System.out.println("-".repeat(50));
            ProfileDemo.demo();
            System.out.println();

            // ==================== 第七部分：演示ResourceLoader ====================
            // ResourceLoader用于加载各种类型的资源（文件、类路径资源、URL资源等）
            System.out.println("【第七部分】ResourceLoader 示例");
            System.out.println("-".repeat(50));
            ResourceLoaderDemo.demo();
            System.out.println();

            System.out.println("========================================");
            System.out.println("所有演示完成！");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("演示过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
