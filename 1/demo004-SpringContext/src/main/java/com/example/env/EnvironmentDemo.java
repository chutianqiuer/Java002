package com.example.env;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * Environment 接口示例类
 *
 * 【功能说明】
 * Environment是Spring应用环境的抽象接口，继承自PropertyResolver接口
 * 它提供了访问应用程序环境和配置属性的能力
 *
 * 【核心功能】
 * 1. 获取激活的Profile列表（开发、测试、生产环境）
 * 2. 获取配置属性值（application.properties、命令行参数、环境变量等）
 * 3. 判断某个Profile是否激活
 * 4. 解析属性占位符（${...}格式）
 * 5. 添加自定义PropertySource
 *
 * 【Profile概念】
 * Profile是Spring提供的环境隔离机制，用于在不同环境下激活不同的Bean配置
 * 常见的Profile：
 * - dev：开发环境
 * - test：测试环境
 * - staging：预发布环境
 * - prod：生产环境
 *
 * 【何时使用Environment】
 * 1. 需要根据环境获取不同配置
 * 2. 需要访问系统属性、环境变量
 * 3. 需要动态获取配置属性
 * 4. 需要判断某个Profile是否激活
 * 5. 需要自定义属性源
 *
 * 【与@Value的区别】
 * - @Value：用于在Bean中注入单个属性值
 * - Environment：提供更强大的属性访问能力，适合编程式使用
 * - @ConfigurationProperties：用于批量绑定配置属性到Bean
 *
 * 【获取Environment的方式】
 * 1. 注入到Bean中：@Autowired private Environment env;
 * 2. 从ApplicationContext获取：context.getEnvironment()
 * 3. 从ConfigurableApplicationContext获取：((ConfigurableApplicationContext)context).getEnvironment()
 */
public class EnvironmentDemo {

    /**
     * 演示Environment接口的各种功能
     *
     * 本方法展示了：
     * 1. 获取激活的Profile
     * 2. 获取配置属性
     * 3. 判断Profile是否激活
     * 4. 添加自定义PropertySource
     */
    public static void demo() {
        System.out.println(">>> 演示Environment接口的使用");

        // 创建AnnotationConfigApplicationContext容器
        // 使用Config类作为配置类（它会被@Component注解扫描到）
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 不使用扫描，直接注册Config类
        context.register(com.example.model.Config.class);
        context.refresh();

        // 获取Environment对象
        // ApplicationContext继承自EnvironmentCapable接口
        // ConfigurableApplicationContext提供了获取ConfigurableEnvironment的能力
        ConfigurableEnvironment environment = context.getEnvironment();

        // 第一部分：Profile相关操作
        System.out.println();
        System.out.println("1. Profile相关操作：");

        // 获取当前激活的Profile列表
        // Profile可以通过多种方式激活：
        // - 编程式：context.getEnvironment().setActiveProfiles("dev")
        // - 命令行：-Dspring.profiles.active=dev
        // - 环境变量：SPRING_PROFILES_ACTIVE=dev
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("   1.1 当前激活的Profiles:");
        if (activeProfiles.length > 0) {
            for (String profile : activeProfiles) {
                System.out.println("       - " + profile);
            }
        } else {
            System.out.println("       （没有激活任何Profile）");
        }

        // 获取默认的Profile列表
        // 当没有任何Profile激活时使用默认Profile
        String[] defaultProfiles = environment.getDefaultProfiles();
        System.out.println("   1.2 默认的Profiles:");
        for (String profile : defaultProfiles) {
            System.out.println("       - " + profile);
        }

        // 判断某个Profile是否激活
        System.out.println("   1.3 判断Profile是否激活:");
        System.out.println("       dev是否激活: " + environment.acceptsProfiles("dev"));
        System.out.println("       prod是否激活: " + environment.acceptsProfiles("prod"));
        System.out.println("       【提示】可以通过设置系统属性来激活Profile：");
        System.out.println("              -Dspring.profiles.active=dev,prod");

        // 第二部分：属性相关操作
        System.out.println();
        System.out.println("2. 属性相关操作：");

        // 获取系统属性
        System.out.println("   2.1 系统属性:");
        System.out.println("       java.version: " + environment.getProperty("java.version"));
        System.out.println("       os.name: " + environment.getProperty("os.name"));
        System.out.println("       user.dir: " + environment.getProperty("user.dir"));

        // 获取系统环境变量
        System.out.println("   2.2 系统环境变量:");
        System.out.println("       JAVA_HOME: " + environment.getProperty("JAVA_HOME", "未设置"));
        System.out.println("       PATH: " +
            (environment.getProperty("PATH", "").length() > 50 ?
             environment.getProperty("PATH", "").substring(0, 50) + "..." :
             environment.getProperty("PATH", "")));

        // 获取属性值，带默认值
        System.out.println("   2.3 带默认值的属性获取:");
        System.out.println("       app.name（存在）: " + environment.getProperty("app.name", "默认值"));
        System.out.println("       app.missing（不存在）: " + environment.getProperty("app.missing", "使用默认值"));

        // 获取属性值，指定类型
        System.out.println("   2.4 指定类型的属性获取:");
        // 将字符串属性值转换为指定类型
        Integer intValue = environment.getProperty("spring.main.banner-mode", Integer.class, 999);
        System.out.println("       spring.main.banner-mode (Integer): " + intValue);

        Boolean boolValue = environment.getProperty("app.config.active", Boolean.class, false);
        System.out.println("       app.config.active (Boolean): " + boolValue);

        // 第三部分：PropertySource操作
        System.out.println();
        System.out.println("3. PropertySource操作：");

        // 获取PropertySource列表
        System.out.println("   3.1 PropertySource列表:");
        Iterable<PropertySource<?>> propertySources = environment.getPropertySources();
        for (PropertySource<?> ps : propertySources) {
            System.out.println("       - " + ps.getName() + " (来源: " + ps.getSource().getClass().getSimpleName() + ")");
        }

        // 第四部分：属性占位符解析
        System.out.println();
        System.out.println("4. 属性占位符解析：");
        System.out.println("   4.1 解析格式：${property.key:default_value}");
        System.out.println("   4.2 解析示例：");

        // 使用resolveRequiredPlaceholders解析占位符
        // 格式：${key:default_value}，如果key不存在则使用default_value
        String resolved = environment.resolveRequiredPlaceholders("${app.name:默认应用}");
        System.out.println("       app.name解析结果: " + resolved);

        String resolvedMissing = environment.resolveRequiredPlaceholders("${app.missing:这是默认值}");
        System.out.println("       app.missing解析结果: " + resolvedMissing);

        // 第五部分：自定义PropertySource
        System.out.println();
        System.out.println("5. 添加自定义PropertySource：");
        System.out.println("   5.1 方式一：从属性文件添加");
        System.out.println("       PropertySource source = new ResourcePropertySource(\"classpath:application.properties\");");
        System.out.println("       environment.getPropertySources().addFirst(source);");

        System.out.println("   5.2 方式二：直接添加键值对");
        System.out.println("       Map<String, Object> myProperties = new HashMap<>();");
        System.out.println("       myProperties.put(\"custom.key\", \"customValue\");");
        System.out.println("       environment.getPropertySources().addLast(");
        System.out.println("           new MapPropertySource(\"myProperties\", myProperties));");

        // 实际演示添加自定义PropertySource
        try {
            // 从classpath添加属性源
            ResourcePropertySource customSource = new ResourcePropertySource("customSource", "classpath:application.properties");
            environment.getPropertySources().addLast(customSource);
            System.out.println("   5.3 已添加自定义属性源，当前app.name: " + environment.getProperty("app.name"));
        } catch (Exception e) {
            System.out.println("   5.3 添加自定义属性源失败: " + e.getMessage());
        }

        // 第六部分：验证解析优先级
        System.out.println();
        System.out.println("6. 属性解析优先级（后添加的优先级更高）：");
        System.out.println("   systemProperties > systemEnvironment > application.properties > ... > defaults");
        System.out.println("   自定义PropertySource会添加到列表末尾，优先级仅次于systemProperties");

        // 关闭容器
        context.close();

        System.out.println();
        System.out.println("<<< Environment接口演示结束");
    }
}
