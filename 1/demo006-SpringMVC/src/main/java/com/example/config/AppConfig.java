package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 应用配置类
 *
 * 本类替代了传统的web.xml配置，实现了Spring容器的Java配置方式。
 *
 * 传统web.xml配置 vs Java配置：
 * - 传统方式：使用web.xml注册Servlet、Filter、Listener
 * - Java配置：使用 @Configuration 注解的类替代web.xml
 *
 * @Configuration 注解的类会被Spring作为Bean定义来源处理，
 * 类中的 @Bean 方法会被当作Bean注册到容器中。
 *
 * @EnableWebMvc 注解启用Spring MVC的默认配置，相当于XML中的 <mvc:annotation-driven/>
 */
@Configuration
/**
 * @ComponentScan 配置组件扫描
 *
 * basePackages 属性指定要扫描的包路径，Spring会自动扫描该包及其子包，
 * 找出所有标注了 @Component、@Service、@Repository、@Controller 的类，
 * 并将它们注册为Spring Bean。
 *
 * 这里扫描 com.example 包下的所有组件，包括：
 * - @Controller 注解的控制器类
 * - @Service 注解的服务类
 * - @Repository 注解的DAO类
 * - @Component 注解的通用组件
 */
@ComponentScan(basePackages = "com.example")
@EnableWebMvc
public class AppConfig {

    /**
     * 本类作为Java配置类，主要职责：
     *
     * 1. 定义Spring容器配置
     *    - 使用 @Configuration 注解标识此类为配置类
     *    - 替代传统的 applicationContext.xml
     *
     * 2. 启用组件扫描
     *    - @ComponentScan 指定扫描包路径
     *    - Spring自动扫描并注册Bean
     *
     * 3. 启用Spring MVC
     *    - @EnableWebMvc 启用MVC的默认配置
     *    - 包括：数据绑定、数字格式化、验证等
     *
     * 4. 注册额外Bean
     *    - 可以在此类中定义 @Bean 方法来注册自定义Bean
     *    - 例如：数据源、事务管理器等
     *
     * 注意：本类的配置会被 WebConfig 类中的配置覆盖或补充
     * WebConfig 继承自 WebMvcConfigurer 接口，用于定制Spring MVC配置
     */
    public AppConfig() {
    }
}
