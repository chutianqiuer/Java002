package com.example.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 生命周期配置类
 *
 * 本类演示如何在@Configuration类中配置Bean的初始化和销毁方法
 *
 * 【@Bean的initMethod和destroyMethod属性】
 *
 * 除了使用注解（@PostConstruct、@PreDestroy）和接口方式外
 * 还可以通过@Bean的initMethod和destroyMethod属性来指定初始化和销毁方法
 *
 * 【优点】
 * 1. 配置和Bean类分离，Bean类不需要添加任何Spring特定注解
 * 2. 可以在一个地方统一管理多个Bean的初始化逻辑
 * 3. 适用于无法修改Bean类的场景（如使用第三方库的类）
 *
 * 【缺点】
 * 1. 配置分散在两个地方
 * 2. 方法名称可以自定义，但需要确保方法签名正确
 *
 * 【方法签名要求】
 * - 初始化方法：必须返回void，不带参数，或者带一个BeanPostProcessor参数
 * - 销毁方法：必须返回void，不带参数
 */
@Configuration
public class LifecycleConfig {

    /**
     * 定义Product Bean，使用@Bean的initMethod和destroyMethod
     *
     * 【参数说明】
     * - initMethod = "initByCustomMethod": 指定初始化方法为Product类的initByCustomMethod()
     * - destroyMethod = "destroyByCustomMethod": 指定销毁方法为Product类的destroyByCustomMethod()
     *
     * 【执行顺序】
     * 1. 构造方法
     * 2. 属性注入（setName, setPrice）
     * 3. @PostConstruct注解的方法
     * 4. InitializingBean.afterPropertiesSet()
     * 5. initByCustomMethod()  <-- 自定义的init-method
     * 6. BeanPostProcessor.postProcessAfterInitialization()
     * 7. 使用中...
     * 8. 容器关闭时：@PreDestroy -> DisposableBean.destroy() -> destroyByCustomMethod()
     */
    @Bean(name = "product", initMethod = "initByCustomMethod", destroyMethod = "destroyByCustomMethod")
    public Product product() {
        Product p = new Product();
        p.setName("笔记本电脑");
        p.setPrice(5999.99);
        return p;
    }

    /**
     * 定义Order Bean（只使用接口方式，不使用@Bean的initMethod/destroyMethod）
     *
     * Order类实现了InitializingBean和DisposableBean接口
     * 所以它的初始化和销毁会通过接口方式完成
     */
    @Bean(name = "order")
    public Order order() {
        Order o = new Order();
        o.setOrderId("ORD20240101001");
        o.setCustomerName("张三");
        return o;
    }
}
