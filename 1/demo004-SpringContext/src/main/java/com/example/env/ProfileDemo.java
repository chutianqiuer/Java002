package com.example.env;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @Profile 注解示例类
 *
 * 【功能说明】
 * @Profile注解用于声明某个Bean或配置类属于特定的环境
 * 只有当对应的Profile被激活时，这些Bean才会被注册到容器中
 *
 * 【使用场景】
 * 1. 开发环境使用内存数据库，生产环境使用真实数据库
 * 2. 不同环境使用不同的配置值
 * 3. 开发和生产环境使用不同的服务地址
 * 4. 启用或禁用某些功能模块
 *
 * 【激活Profile的方式】
 * 1. 编程式：context.getEnvironment().setActiveProfiles("dev")
 * 2. 命令行参数：-Dspring.profiles.active=dev
 * 3. 环境变量：SPRING_PROFILES_ACTIVE=dev
 * 4. JVM系统属性：-Dspring.profiles.active=dev
 *
 * 【@Profile的语法】
 * - @Profile("dev")：仅在dev环境激活
 * - @Profile({"dev", "test"})：在dev或test环境激活
 * - @Profile("!prod")：在非prod环境激活
 * - @Profile 默认Profile：当没有任何Profile激活时使用
 *
 * 【与@Conditional的区别】
 * @Profile：基于环境（Profile）条件判断
 * @Conditional：基于任意条件判断，更灵活
 *
 * 【注意事项】
 * - @Profile标注在@Configuration类上时，整个类的所有@Bean都会受该Profile控制
 * - @Profile标注在@Component类上时，只有该Bean受Profile控制
 * - 可以同时使用多个@Profile注解（OR关系）
 */
public class ProfileDemo {

    /**
     * 演示@Profile注解的各种用法
     *
     * 本方法展示了：
     * 1. 如何定义不同环境的Bean
     * 2. 如何激活特定Profile
     * 3. @Profile的条件判断逻辑
     */
    public static void demo() {
        System.out.println(">>> 演示@Profile注解的使用");

        // 第一部分：Profile配置说明
        System.out.println();
        System.out.println("1. @Profile注解的用途：");
        System.out.println("   - 根据不同环境（dev/test/prod）激活不同的Bean");
        System.out.println("   - 实现配置隔离，避免环境差异导致的问题");
        System.out.println("   - 提高应用的可移植性和可测试性");

        // 第二部分：创建不同环境的配置类
        System.out.println();
        System.out.println("2. 不同环境的Bean定义示例：");

        System.out.println("   @Configuration");
        System.out.println("   public class DataSourceConfig {");
        System.out.println("");
        System.out.println("       @Bean @Profile(\"dev\") // 仅开发环境激活");
        System.out.println("       public DataSource devDataSource() {");
        System.out.println("           // 返回H2内存数据库");
        System.out.println("       }");
        System.out.println("");
        System.out.println("       @Bean @Profile(\"prod\") // 仅生产环境激活");
        System.out.println("       public DataSource prodDataSource() {");
        System.out.println("           // 返回MySQL数据源");
        System.out.println("       }");
        System.out.println("   }");

        // 第三部分：激活Profile的多种方式
        System.out.println();
        System.out.println("3. 激活Profile的方式：");

        System.out.println("   方式一：编程式（推荐用于演示）");
        System.out.println("       ConfigurableEnvironment env = context.getEnvironment();");
        System.out.println("       env.setActiveProfiles(\"dev\", \"debug\");");

        System.out.println();
        System.out.println("   方式二：命令行参数");
        System.out.println("       java -jar app.jar --spring.profiles.active=dev,prod");

        System.out.println();
        System.out.println("   方式三：环境变量");
        System.out.println("       export SPRING_PROFILES_ACTIVE=dev");
        System.out.println("       # 或在JVM启动时");
        System.out.println("       java -Dspring.profiles.active=dev -jar app.jar");

        System.out.println();
        System.out.println("   方式四：application.properties");
        System.out.println("       spring.profiles.active=dev");

        // 第四部分：实际演示
        System.out.println();
        System.out.println("4. 实际演示：");

        // 创建容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 获取Environment并设置Profile
        ConfigurableEnvironment env = context.getEnvironment();

        // 方式一：使用数组同时激活多个Profile
        System.out.println("   4.1 激活Profile: dev");
        env.setActiveProfiles("dev");

        // 注册配置类并刷新
        context.register(DataSourceConfig.class);
        context.refresh();

        // 检查激活的Profile
        System.out.println("       激活的Profiles: " + String.join(", ", env.getActiveProfiles()));

        // 检查哪些Bean被创建了
        System.out.println("       容器中的Bean:");
        String[] beanNames = context.getBeanDefinitionNames();
        for (String name : beanNames) {
            // 过滤掉Spring内部的Bean
            if (!name.startsWith("org.springframework")) {
                System.out.println("         - " + name);
            }
        }

        // 关闭容器
        context.close();

        // 第五部分：多Profile演示
        System.out.println();
        System.out.println("5. 多个Profile组合演示：");

        // 创建一个新容器，激活多个Profile
        AnnotationConfigApplicationContext context2 = new AnnotationConfigApplicationContext();
        ConfigurableEnvironment env2 = context2.getEnvironment();
        System.out.println("   5.1 激活Profile: {dev, debug}");
        env2.setActiveProfiles("dev", "debug");

        context2.register(DataSourceConfig.class);
        context2.refresh();

        System.out.println("       激活的Profiles: " + String.join(", ", env2.getActiveProfiles()));

        // 尝试获取不同环境的Bean
        System.out.println("       尝试获取devDebugDataSource Bean:");
        try {
            Object devDebugBean = context2.getBean("devDebugDataSource");
            System.out.println("       获取成功: " + devDebugBean);
        } catch (Exception e) {
            System.out.println("       获取失败: " + e.getMessage());
        }

        context2.close();

        // 第六部分：默认Profile
        System.out.println();
        System.out.println("6. 默认Profile（default）演示：");

        // 当没有任何Profile激活时，会使用默认Profile
        // 可以通过@Profile("default")或省略来指定默认Bean
        System.out.println("   6.1 没有激活任何Profile时：");
        System.out.println("       使用@Profile(\"default\")标注的Bean");

        AnnotationConfigApplicationContext context3 = new AnnotationConfigApplicationContext();
        ConfigurableEnvironment env3 = context3.getEnvironment();
        System.out.println("       激活的Profiles: " +
            (env3.getActiveProfiles().length > 0 ?
             String.join(", ", env3.getActiveProfiles()) : "（无）"));
        System.out.println("       默认Profiles: " + String.join(", ", env3.getDefaultProfiles()));

        context3.register(DataSourceConfig.class);
        context3.refresh();

        System.out.println("       容器中的Bean:");
        String[] defaultBeanNames = context3.getBeanDefinitionNames();
        for (String name : defaultBeanNames) {
            if (!name.startsWith("org.springframework")) {
                System.out.println("         - " + name);
            }
        }

        context3.close();

        // 第七部分：Profile否定语法
        System.out.println();
        System.out.println("7. Profile否定语法：");
        System.out.println("   @Profile(\"!prod\") 表示非生产环境");
        System.out.println("   这在某些场景下很有用，例如：");
        System.out.println("   - @Profile(\"!prod\") 用于开发/测试环境的额外日志");
        System.out.println("   - @Profile(\"!test\") 用于非测试环境的特殊配置");

        System.out.println();
        System.out.println("<<< @Profile注解演示结束");
    }

    /**
     * 数据源配置类 - 用于演示@Profile注解
     *
     * 此类展示了如何为不同环境定义不同的Bean
     */
    @Configuration
    public static class DataSourceConfig {

        /**
         * 开发环境数据源
         * 仅当dev Profile激活时此Bean才会被创建
         */
        @Bean
        @Profile("dev")
        public String devDataSource() {
            System.out.println("       [DataSourceConfig] 创建devDataSource Bean");
            return "H2内存数据库 (开发环境)";
        }

        /**
         * 生产环境数据源
         * 仅当prod Profile激活时此Bean才会被创建
         */
        @Bean
        @Profile("prod")
        public String prodDataSource() {
            System.out.println("       [DataSourceConfig] 创建prodDataSource Bean");
            return "MySQL数据库 (生产环境)";
        }

        /**
         * 测试环境数据源
         * 仅当test Profile激活时此Bean才会被创建
         */
        @Bean
        @Profile("test")
        public String testDataSource() {
            System.out.println("       [DataSourceConfig] 创建testDataSource Bean");
            return "H2内存数据库 (测试环境)";
        }

        /**
         * 开发+调试模式数据源
         * 当dev和debug Profile同时激活时此Bean才会被创建
         */
        @Bean
        @Profile({"dev", "debug"})
        public String devDebugDataSource() {
            System.out.println("       [DataSourceConfig] 创建devDebugDataSource Bean");
            return "H2内存数据库 (开发+调试模式)";
        }

        /**
         * 默认数据源
         * 当没有任何Profile激活时此Bean才会被创建
         */
        @Bean
        @Profile("default")
        public String defaultDataSource() {
            System.out.println("       [DataSourceConfig] 创建defaultDataSource Bean");
            return "默认数据源";
        }

        /**
         * 非生产环境数据源
         * 使用否定语法，当prod Profile未激活时此Bean才会被创建
         */
        @Bean
        @Profile("!prod")
        public String nonProdDataSource() {
            System.out.println("       [DataSourceConfig] 创建nonProdDataSource Bean");
            return "非生产环境数据源";
        }
    }
}
