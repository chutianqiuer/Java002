package com.example.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Product类 - 演示Spring Bean生命周期回调的多种实现方式
 *
 * 【Spring Bean生命周期概述】
 *
 *  Bean的创建 → 初始化 → 使用 → 销毁
 *
 * 【初始化回调的多种方式】
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ 方式一：@PostConstruct注解（JSR-250标准注解）                              │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 优点：                                                                    │
 * │   • 清晰直观，标注在方法上，一看就知道是初始化方法                           │
 * │   • 依赖JDK标准，不依赖Spring特定接口                                      │
 * │   • 可以在任何POJO上使用                                                  │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 需要引入jakarta.annotation-api依赖（Spring 6.x）                       │
 * │   • 只能注解一个方法（通常一个类只用一个初始化方法）                        │
 * │   • 无法控制初始化顺序（如果需要顺序，可以使用@DependsOn）                  │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ 方式二：InitializingBean接口                                              │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 优点：                                                                    │
 * │   • Spring原生接口，兼容性更好                                            │
 * │   • 可以在afterPropertiesSet()方法中访问Bean属性                          │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 耦合Spring特定接口，不便于替换Spring容器                               │
 * │   • 使用起来不如注解直观                                                  │
 * │   • 需要实现接口方法，即使不需要初始化也要实现                             │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ 方式三：@Bean的init-method属性                                            │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 优点：                                                                    │
 * │   • 配置和代码分离                                                       │
 * │   • 可以在@Configuration类中统一管理初始化逻辑                            │
 * │   • 适用于不想在Bean类上添加任何注解的场景                                │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 需要在@Configuration类中额外配置                                      │
 * │   • 初始化逻辑分散在两个地方（Bean类和Config类）                          │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * 【销毁回调的多种方式】
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ 方式一：@PreDestroy注解（JSR-250标准注解）                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 优点：                                                                    │
 * │   • 清晰直观，标注在方法上                                                │
 * │   • 依赖JDK标准，不依赖Spring特定接口                                     │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 对于singleton Bean有效，prototype Bean不会自动调用                   │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ 方式二：DisposableBean接口                                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 优点：                                                                    │
 * │   • Spring原生接口，兼容性更好                                            │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 耦合Spring特定接口                                                    │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ 方式三：@Bean的destroy-method属性                                         │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 优点：                                                                    │
 * │   • 配置和代码分离                                                       │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 需要在@Configuration类中额外配置                                     │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * 【执行顺序】
 * 当多种初始化/销毁方式同时存在时，Spring的执行顺序是：
 * 1. 构造方法
 * 2. 属性注入
 * 3. BeanPostProcessor.postProcessBeforeInitialization()
 * 4. @PostConstruct注解的方法
 * 5. InitializingBean.afterPropertiesSet()
 * 6. 自定义的init-method
 * 7. BeanPostProcessor.postProcessAfterInitialization()
 *
 * 【注意事项】
 * - prototype Bean的销毁回调不会被Spring自动调用，需要调用者自行销毁
 * - 建议优先使用@PostConstruct和@PreDestroy，代码更清晰
 */
public class Product implements InitializingBean, DisposableBean {

    private String name;
    private double price;

    /**
     * 默认构造方法
     */
    public Product() {
        System.out.println("【Product构造方法】创建Product实例 - " + this.hashCode());
    }

    /**
     * 设置商品名称
     */
    public void setName(String name) {
        System.out.println("【Product.setName】设置商品名称: " + name);
        this.name = name;
    }

    /**
     * 设置商品价格
     */
    public void setPrice(double price) {
        System.out.println("【Product.setPrice】设置商品价格: " + price);
        this.price = price;
    }

    /**
     * 获取商品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取商品价格
     */
    public double getPrice() {
        return price;
    }

    // ==================== 初始化回调方式 ====================

    /**
     * 方式一：@PostConstruct注解的初始化方法
     *
     * 这个注解来自jakarta.annotation包（JSR-250标准）
     * 【推荐优先使用】这种方式，代码清晰，不耦合Spring
     *
     * 执行时机：在所有属性注入完成后，Bean正式使用前调用
     */
    @PostConstruct
    public void initByAnnotation() {
        System.out.println("【Product.@PostConstruct】@PostConstruct初始化方法被调用");
        // 在这里可以对属性进行最后的校验或转换
        if (name != null && name.isEmpty()) {
            this.name = "未命名商品";
        }
        System.out.println("【Product.@PostConstruct】初始化完成，商品信息: name=" + name + ", price=" + price);
    }

    /**
     * 方式二：实现InitializingBean接口
     *
     * 这个接口来自org.springframework.beans.factory包
     * 当Bean的所有属性被设置完成后，Spring会自动调用这个方法
     *
     * 【不推荐使用】：因为耦合了Spring特定接口
     * 适用于需要访问Bean属性或进行复杂初始化的场景
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("【Product.InitializingBean】afterPropertiesSet()方法被调用");
        // 在这里可以进行属性校验或初始化
        System.out.println("【Product.InitializingBean】InitializingBean初始化完成，商品信息: name=" + name + ", price=" + price);
    }

    /**
     * 方式三：自定义的init-method（通过@Bean的initMethod属性指定）
     * 这个方法会在ProductConfig中通过@Bean的initMethod属性指定
     */
    public void initByCustomMethod() {
        System.out.println("【Product.initMethod】自定义init-method被调用");
        System.out.println("【Product.initMethod】自定义初始化完成，商品信息: name=" + name + ", price=" + price);
    }

    // ==================== 销毁回调方式 ====================

    /**
     * 方式一：@PreDestroy注解的销毁方法
     *
     * 这个注解来自jakarta.annotation包（JSR-250标准）
     * 【推荐优先使用】这种方式，代码清晰，不耦合Spring
     *
     * 执行时机：在Bean被销毁前调用
     * 【注意】：只对singleton Bean有效，prototype Bean不会自动调用
     */
    @PreDestroy
    public void destroyByAnnotation() {
        System.out.println("【Product.@PreDestroy】@PreDestroy销毁方法被调用");
        System.out.println("【Product.@PreDestroy】清理商品资源: name=" + name);
    }

    /**
     * 方式二：实现DisposableBean接口
     *
     * 这个接口来自org.springframework.beans.factory包
     * 当Bean被销毁时，Spring会自动调用这个方法
     *
     * 【不推荐使用】：因为耦合了Spring特定接口
     */
    @Override
    public void destroy() throws Exception {
        System.out.println("【Product.DisposableBean】destroy()方法被调用");
        System.out.println("【Product.DisposableBean】DisposableBean销毁完成");
    }

    /**
     * 方式三：自定义的destroy-method（通过@Bean的destroyMethod属性指定）
     * 这个方法会在ProductConfig中通过@Bean的destroyMethod属性指定
     */
    public void destroyByCustomMethod() {
        System.out.println("【Product.destroyMethod】自定义destroy-method被调用");
        System.out.println("【Product.destroyMethod】自定义销毁完成");
    }

    /**
     * toString方法
     */
    @Override
    public String toString() {
        return "Product{" +
                "hashCode=" + this.hashCode() +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
