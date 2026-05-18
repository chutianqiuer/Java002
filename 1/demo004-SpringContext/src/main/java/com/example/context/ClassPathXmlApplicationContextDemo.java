package com.example.context;

import com.example.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * ClassPathXmlApplicationContext 示例类
 *
 * 【功能说明】
 * ClassPathXmlApplicationContext是ApplicationContext接口的实现类之一
 * 它从classpath（类路径）中加载XML配置文件来创建Spring容器
 *
 * 【工作原理】
 * 1. 构造函数接收classpath中的XML配置文件路径
 * 2. 解析XML配置文件，获取Bean定义信息
 * 3. 使用BeanDefinitionReader读取并解析Bean定义
 * 4. 实例化所有单例Bean（默认行为）
 * 5. 注册Bean后处理器（BeanPostProcessor）
 * 6. 容器准备就绪，可以提供Bean获取服务
 *
 * 【与FileSystemXmlApplicationContext的区别】
 * - ClassPathXmlApplicationContext: 从classpath加载，适合打包到jar/war中的配置文件
 * - FileSystemXmlApplicationContext: 从文件系统路径加载，适合在文件系统任意位置的配置文件
 *
 * 【何时使用】
 * 1. 配置文件放在src/main/resources目录下
 * 2. 配置文件需要随应用一起打包
 * 3. 不需要灵活变更配置文件位置的场景
 * 4. 微服务架构中配置中心化管理
 *
 * 【优缺点】
 * 优点：
 * - 配置简单方便，不需要指定绝对路径
 * - 配置文件随应用一起部署，易于管理
 * - 适合类加载器能够访问的资源
 *
 * 缺点：
 * - 配置文件必须在classpath中
 * - 修改配置文件后需要重新编译打包
 * - 不适合需要频繁变更配置的场景
 */
public class ClassPathXmlApplicationContextDemo {

    /**
     * 演示ClassPathXmlApplicationContext的基本用法
     *
     * 本方法展示了：
     * 1. 如何创建ClassPathXmlApplicationContext实例
     * 2. 如何从容器中获取Bean
     * 3. Bean的作用域（singleton vs prototype）
     * 4. 容器的关闭和资源释放
     */
    public static void demo() {
        System.out.println(">>> 演示ClassPathXmlApplicationContext的使用");

        // 第一步：创建ApplicationContext容器
        // 方式一：直接传入classpath路径字符串
        // 注意：beans.xml文件位于src/main/resources目录下
        //       编译后会复制到target/classes目录下，即classpath的根目录
        System.out.println("1. 创建ClassPathXmlApplicationContext容器...");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

        // 方式二：也可以使用数组同时加载多个配置文件
        // ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        //     new String[]{"beans.xml", "beans2.xml"}
        // );

        // 方式三：使用Class类型作为参数，让Spring自动查找
        // ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        //     "beans.xml",
        //     MainApp.class.getClassLoader()
        // );

        System.out.println("   容器创建成功！");

        // 第二步：从容器中获取Bean
        // getBean方法有多种重载形式：
        // - getBean(String id): 通过Bean的id获取
        // - getBean(String id, Class<T> type): 通过id和类型获取，更安全
        // - getBean(Class<T> type): 通过类型获取（要求该类型只有一个Bean）
        // - getBean(String id, Object... args): 获取prototype作用域的Bean时传入构造参数

        System.out.println();
        System.out.println("2. 从容器获取Bean示例：");

        // 通过Bean的id获取（最常用的方式）
        System.out.println("   2.1 通过Bean ID获取（xmlConfigBean）:");
        Config config1 = (Config) context.getBean("xmlConfigBean");
        System.out.println("       获取到的Bean: " + config1);
        System.out.println("       消息内容: " + config1.getMessage());

        // 通过id和类型获取（推荐方式，更安全）
        System.out.println("   2.2 通过Bean ID和类型获取（configWithConstructor）:");
        Config config2 = context.getBean("configWithConstructor", Config.class);
        System.out.println("       获取到的Bean: " + config2);
        System.out.println("       消息内容: " + config2.getMessage());

        // 第三步：演示Bean作用域
        System.out.println();
        System.out.println("3. Bean作用域演示：");

        // singleton作用域：每次获取的都是同一个实例
        System.out.println("   3.1 Singleton作用域Bean:");
        Config singleton1 = context.getBean("singletonBean", Config.class);
        Config singleton2 = context.getBean("singletonBean", Config.class);
        System.out.println("       singleton1地址: " + System.identityHashCode(singleton1));
        System.out.println("       singleton2地址: " + System.identityHashCode(singleton2));
        System.out.println("       是否为同一实例: " + (singleton1 == singleton2));

        // prototype作用域：每次获取都会创建新实例
        System.out.println("   3.2 Prototype作用域Bean:");
        Config prototype1 = context.getBean("prototypeBean", Config.class);
        Config prototype2 = context.getBean("prototypeBean", Config.class);
        System.out.println("       prototype1地址: " + System.identityHashCode(prototype1));
        System.out.println("       prototype2地址: " + System.identityHashCode(prototype2));
        System.out.println("       是否为同一实例: " + (prototype1 == prototype2));

        // 第四步：演示容器的功能
        System.out.println();
        System.out.println("4. 容器功能演示：");

        // 判断容器是否包含某个Bean
        System.out.println("   4.1 判断容器是否包含指定Bean:");
        System.out.println("       包含xmlConfigBean: " + context.containsBean("xmlConfigBean"));
        System.out.println("       包含不存在的Bean: " + context.containsBean("nonExistentBean"));

        // 获取Bean的类型
        System.out.println("   4.2 获取Bean的类型:");
        System.out.println("       xmlConfigBean的类型: " + context.getType("xmlConfigBean").getName());

        // 获取Bean的别名
        System.out.println("   4.3 获取Bean的别名:");
        String[] aliases = context.getAliases("xmlConfigBean");
        System.out.println("       xmlConfigBean的别名: " +
            (aliases.length > 0 ? String.join(", ", aliases) : "无"));

        // 第五步：关闭容器
        // 注意：关闭容器只会销毁单例Bean，prototype作用域的Bean需要自行管理
        System.out.println();
        System.out.println("5. 关闭容器:");
        context.close();
        System.out.println("   容器已关闭！");

        System.out.println();
        System.out.println("<<< ClassPathXmlApplicationContext演示结束");
    }
}
