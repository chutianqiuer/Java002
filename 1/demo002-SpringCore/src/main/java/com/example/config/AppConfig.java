/*
 * =====================================================
 * Spring Java配置类：AppConfig
 * =====================================================
 *
 * 【Java配置方式介绍】
 *
 * Spring支持两种配置方式：
 * 1. XML配置（传统方式）
 * 2. Java配置（推荐方式）
 *
 * Java配置相比XML配置的优势：
 * - 类型安全：编译时检查错误
 * - 强大的IDE支持：代码补全、重构
 * - 动态配置：可以在配置中进行逻辑判断
 * - 易于测试：可以轻松创建测试配置
 * - 面向对象：可以使用继承、多态等OOP特性
 *
 * 【@Configuration注解】
 *
 * @Configuration标注的类是配置类，Spring会特殊处理：
 * - 类中标注@Bean的方法会被代理
 * - 多次调用同一个@Bean方法，返回同一个实例（单例）
 * - 可以在方法中调用其他@Bean方法，获取代理后的结果
 *
 * 【@Bean注解】
 *
 * @Bean标注的方法会创建一个Spring bean：
 * - 方法名默认是bean的名称（可以通过name属性指定多个名称）
 * - 返回类型是bean的类型
 * - Spring会调用这个方法获取bean实例
 *
 * 【Bean的命名】
 *
 * 默认情况下，@Bean方法名就是bean的名称。
 * 可以使用@Bean(name="name1, name2")指定多个名称。
 * Bean名称在容器中必须唯一。
 *
 * =====================================================
 */
package com.example.config;

import com.example.di.Consumer;
import com.example.di.EmailService;
import com.example.di.MessageService;
import com.example.di.SmsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 应用程序配置类
 *
 * 这个类替代了传统的XML配置文件，
 * 使用Java代码定义Spring容器的bean和配置。
 *
 * 【配置类的特点】
 *
 * 1. @Configuration让Spring知道这是一个配置类
 * 2. 配置类本身也会被Spring作为bean管理
 * 3. 配置类中的@Bean方法是工厂方法，返回bean实例
 */
@Configuration
public class AppConfig {

    /*
     * ============================================
     * Bean定义区域
     * ============================================
     *
     * 下面定义的所有@Bean方法都会在Spring容器启动时执行，
     * 创建对应的bean并注册到容器中。
     */

    // ============================================
    // 消息服务Bean定义
    // ============================================

    /**
     * 定义EmailService的Bean
     *
     * 【@Bean的工作流程】
     * 1. Spring扫描到@Bean注解
     * 2. 调用emailService()方法
     * 3. 将返回的EmailService实例注册为bean
     * 4. Bean的名称默认为方法名"emailService"
     *
     * 【@Primary注解】
     * 如果MessageService有多个实现类，
     * 使用@Primary标注的实现类会作为默认选择。
     * 这里标注EmailService为主要实现。
     *
     * @return EmailService实例
     */
    @Bean
    @Primary
    public MessageService emailService() {
        /*
         * 这里可以添加额外的配置逻辑
         * 例如：设置邮件服务器的连接参数
         * 或者创建一个代理对象添加日志功能
         */
        System.out.println("【@Bean】创建EmailService实例");
        return new EmailService();
    }

    /**
     * 定义SmsService的Bean
     *
     * 【多实现处理】
     * 因为MessageService有多个实现（EmailService和SmsService），
     * Spring需要知道使用哪个实现进行注入。
     *
     * 有几种解决方案：
     * 1. @Primary：标记主要实现
     * 2. @Qualifier：指定具体bean名称
     * 3. @Autowired + @Qualifier：按名称注入
     *
     * @return SmsService实例
     */
    @Bean
    public MessageService smsService() {
        System.out.println("【@Bean】创建SmsService实例");
        return new SmsService();
    }

    // ============================================
    // Consumer Bean定义
    // ============================================

    /**
     * 定义Consumer的Bean - 演示构造器注入
     *
     * 【构造器注入的实现】
     * Spring会自动分析构造方法参数，
     * 找到MessageService类型的bean并传入。
     *
     * 【工作原理】
     * 1. Spring扫描构造方法参数类型：MessageService
     * 2. 发现有两个MessageService的bean：emailService和smsService
     * 3. 因为emailService标注了@Primary，所以注入emailService
     * 4. 调用构造方法，创建Consumer实例
     *
     * 【Bean名称】
     * 这个方法没有指定name属性，
     * 所以默认使用方法名"constructorInjectionConsumer"作为bean名称。
     *
     * @param service Spring自动注入的MessageService实现
     * @return Consumer实例
     */
    @Bean
    public Consumer constructorInjectionConsumer(MessageService service) {
        System.out.println("【@Bean】创建Consumer实例（使用构造器注入）");
        System.out.println("【@Bean】注入的MessageService类型: " + service.getClass().getSimpleName());
        return new Consumer(service);
    }

    /**
     * 定义Consumer的Bean - 演示Setter注入
     *
     * 【Setter注入的实现】
     * 这个方法没有MessageService参数，
     * 而是依赖@Bean方法返回的Consumer实例通过setter注入。
     *
     * 但实际上，Consumer的setter注入是在Consumer类内部通过@Autowired完成的，
     * 不需要在这里特别处理。
     *
     * 这个bean的存在主要是为了演示可以通过不同方式创建多个Consumer实例，
     * 每个实例可以注入不同的MessageService实现。
     *
     * 【Bean命名冲突】
     * 注意：两个@Bean方法不能返回同一个类型且名称相同。
     * 这里使用不同的方法名来避免冲突。
     *
     * @param service Spring自动注入的MessageService实现
     * @return Consumer实例
     */
    @Bean
    public Consumer setterInjectionConsumer(MessageService service) {
        System.out.println("【@Bean】创建Consumer实例（使用Setter注入）");
        System.out.println("【@Bean】注入的MessageService类型: " + service.getClass().getSimpleName());

        Consumer consumer = new Consumer();
        // 注意：这里手动调用setter进行注入
        // 但实际上，如果Consumer类使用了@Autowired标注setter，
        // Spring会自动完成注入，不需要我们手动调用
        consumer.setService(service);

        return consumer;
    }

    /*
     * ============================================
     * 配置提示和最佳实践
     * ============================================
     *
     * 1. 【Bean作用域】
     *    默认情况下，@Bean创建的是singleton作用域。
     *    如果需要prototype作用域，可以使用@Scope("prototype")。
     *
     * 2. 【Bean懒加载】
     *    默认情况下，@Bean是饿汉式加载（容器启动时创建）。
     *    如果需要懒加载，可以使用@Lazy注解。
     *
     * 3. 【Bean初始化和销毁】
     *    可以使用@PostConstruct和@PreDestroy注解，
     *    或者在@Bean中指定initMethod和destroyMethod属性。
     *
     * 4. 【条件化Bean】
     *    可以使用@Conditional注解根据条件决定是否创建Bean。
     *
     * 5. 【配置类代理】
     *    @Configuration标注的类会被cglib代理，
     *    这是实现@Bean方法单例的关键。
     *    不要在@Bean方法内部直接调用其他@Bean方法，
     *    否则会绕过代理，导致问题。
     */
}
