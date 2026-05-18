package com.example.context;

import com.example.model.Config;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * AnnotationConfigApplicationContext 示例类
 *
 * 【功能说明】
 * AnnotationConfigApplicationContext是ApplicationContext接口的实现类之一
 * 它专门用于加载注解配置类（如@Configuration注解的类）来创建Spring容器
 *
 * 【与ClassPathXmlApplicationContext的区别】
 * - AnnotationConfigApplicationContext: 专门用于处理注解和Java配置类
 * - ClassPathXmlApplicationContext: 用于加载XML配置文件
 * - GenericApplicationContext: 通用容器，支持XML、注解等多种配置方式
 *
 * 【何时使用】
 * 1. 使用Java配置类（@Configuration）代替XML配置文件
 * 2. 使用注解（@Component、@Service、@Repository、@Controller等）定义Bean
 * 3. 使用@ComponentScan自动扫描并注册Bean
 * 4. 需要类型安全的配置（编译期检查）
 * 5. 现代化的Spring应用开发
 *
 * 【核心注解】
 * @Configuration: 标识一个类是配置类，Spring会从这个类获取Bean定义
 * @ComponentScan: 自动扫描指定包下的@Component、@Service等注解
 * @Bean: 在@Configuration类中方法上使用，定义一个Bean
 * @Import: 导入其他配置类
 * @ImportResource: 在Java配置类中导入XML配置
 *
 * 【优缺点】
 * 优点：
 * - 类型安全，编译期即可检查配置错误
 * - 支持复杂配置逻辑（可以在@Bean方法中写Java代码）
 * - 易于重构和版本控制管理
 * - 减少了XML配置文件
 * - 适合现代化开发流程
 *
 * 缺点：
 * - 需要编译环境支持
 * - 不如XML配置灵活（不能运行时变更）
 * - 学习曲线相对较陡
 */
public class AnnotationConfigApplicationContextDemo {

    /**
     * 演示AnnotationConfigApplicationContext的基本用法
     *
     * 本方法展示了：
     * 1. 如何创建AnnotationConfigApplicationContext实例
     * 2. 如何注册配置类
     * 3. 如何使用@ComponentScan自动扫描
     * 4. 如何获取容器中的Bean
     */
    public static void demo() {
        System.out.println(">>> 演示AnnotationConfigApplicationContext的使用");

        // 第一步：创建容器并注册配置类
        System.out.println();
        System.out.println("1. 创建AnnotationConfigApplicationContext容器：");

        // 方式一：直接传入配置类
        // AnnotationConfigApplicationContext context =
        //     new AnnotationConfigApplicationContext(AppConfig.class);

        // 方式二：先创建容器，再注册配置类（允许链式调用）
        System.out.println("   方式一：传入配置类Class对象");
        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext();
        // 注册配置类
        context.register(Config.class);
        System.out.println("   已注册Config类");

        // 方式三：注册多个配置类
        System.out.println("   方式二：注册多个配置类");
        // context.register(Config1.class, Config2.class, Config3.class);

        // 方式四：指定要扫描的包（等价于@ComponentScan）
        System.out.println("   方式三：指定要扫描的包");
        // new AnnotationConfigApplicationContext("com.example.package");
        // new AnnotationConfigApplicationContext("com.example.package1", "com.example.package2");

        // 刷新容器，使所有配置生效
        // 这一步会完成Bean定义加载、Bean实例化、Bean后处理器注册等
        System.out.println("   刷新容器...");
        context.refresh();
        System.out.println("   容器刷新完成！");

        // 第二步：从容器获取Bean
        System.out.println();
        System.out.println("2. 从容器获取Bean：");

        // 通过类型获取（最简单的方式，要求该类型只有一个Bean）
        System.out.println("   2.1 通过类型获取Bean:");
        Config configByType = context.getBean(Config.class);
        System.out.println("       获取到的Config Bean: " + configByType);

        // 通过Bean名称获取
        System.out.println("   2.2 通过Bean名称获取:");
        String[] beanNames = context.getBeanDefinitionNames();
        System.out.println("       容器中所有Bean名称:");
        for (String name : beanNames) {
            System.out.println("         - " + name);
        }

        // 第三步：获取Bean的数量和信息
        System.out.println();
        System.out.println("3. 容器Bean统计：");
        System.out.println("       Bean总数: " + context.getBeanDefinitionCount());
        System.out.println("       Bean名称列表: " + String.join(", ", beanNames));

        // 第四步：演示容器的功能
        System.out.println();
        System.out.println("4. 容器功能演示：");

        // 判断容器是否包含某个Bean
        System.out.println("   4.1 判断容器是否包含指定Bean:");
        System.out.println("       包含Config: " + context.containsBean("config"));
        System.out.println("       包含不存在的Bean: " + context.containsBean("nonExistentBean"));

        // 获取Bean的类型
        System.out.println("   4.2 获取Bean的类型:");
        System.out.println("       config类型: " + context.getType("config").getName());

        // 第五步：Environment功能
        System.out.println();
        System.out.println("5. Environment功能：");
        ConfigurableEnvironment env = context.getEnvironment();
        System.out.println("   当前激活的Profiles: " +
            String.join(", ", env.getActiveProfiles()));
        System.out.println("   默认Profiles: " +
            String.join(", ", env.getDefaultProfiles()));
        System.out.println("   Java版本: " + env.getProperty("java.version"));
        System.out.println("   操作系统: " + env.getProperty("os.name"));

        // 第六步：关闭容器
        System.out.println();
        System.out.println("6. 关闭容器:");
        context.close();
        System.out.println("   容器已关闭！");

        System.out.println();
        System.out.println("<<< AnnotationConfigApplicationContext演示结束");
    }
}
