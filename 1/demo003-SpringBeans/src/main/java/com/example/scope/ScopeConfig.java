package com.example.scope;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Spring Bean 作用域配置类
 *
 * 本类演示Spring中最常用的两种Bean作用域：
 * 1. singleton（单例作用域）- Spring默认的作用域
 * 2. prototype（原型作用域）- 每次获取时创建新实例
 *
 * 【单例作用域 vs 原型作用域的核心区别】
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                        singleton（单例作用域）                            │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 特点：                                                                    │
 * │   • Spring容器中只存在一个Bean实例                                        │
 * │   • 每次从容器获取该Bean时，返回的都是同一个实例                           │
 * │   • Bean在容器创建时就会实例化（默认情况下）                              │
 * │   • Bean的销毁由容器管理                                                  │
 * │                                                                          │
 * │ 适用场景：                                                                │
 * │   • 无状态的Bean（不存储实例状态）                                        │
 * │   • 共享的Bean（需要在多处共享数据）                                      │
 * │   • 服务类、工具类、DAO层等                                               │
 * │   • 配置类、数据源等                                                     │
 * │                                                                          │
 * │ 优点：                                                                    │
 * │   • 减少内存开销                                                          │
 * │   • 提高性能（无需频繁创建对象）                                          │
 * │   • 便于管理Bean生命周期                                                 │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 所有请求共享同一个实例，可能存在线程安全问题                           │
 * │   • 不适合有状态的Bean                                                    │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                       prototype（原型作用域）                            │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 特点：                                                                    │
 * │   • 每次从容器获取该Bean时，都会创建新的实例                              │
 * │   • Bean在首次请求时才会实例化                                            │
 * │   • Spring容器不负责Bean的销毁                                           │
 * │   • 调用者需要自行管理Bean的销毁                                          │
 * │                                                                          │
 * │ 适用场景：                                                                │
 * │   • 有状态的Bean（需要保持各自独立的状态）                                │
 * │   • 每个请求需要独立实例的场景                                            │
 * │   • Web中的Controller、Action等                                          │
 * │   • 需要频繁创建和销毁的对象                                             │
 * │                                                                          │
 * │ 优点：                                                                    │
 * │   • 每个请求拥有独立的实例，不存在线程安全问题                             │
 * │   • 适合有状态的对象                                                      │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 每次请求都会创建新对象，增加内存开销                                   │
 * │   • 需要调用者自行管理Bean的销毁                                          │
 * │   • 不利于GC（频繁创建对象）                                              │
 * └─────────────────────────────────────────────────────────────────────────┘
 */
@Configuration
public class ScopeConfig {

    /**
     * 定义一个单例作用域的Bean
     *
     * @Scope("singleton") 或 @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
     * 表示该Bean的作用域为单例
     *
     * 【重要】：默认情况下，Spring容器中的Bean都是单例的
     * 即使不添加@Scope注解，默认就是singleton作用域
     *
     * @return SingletonBean 实例
     */
    @Bean
    @Scope("singleton")  // 显式指定单例作用域（可选，因为是默认值）
    public SingletonBean singletonBean() {
        // 当执行这行代码时，会打印"创建SingletonBean实例"
        // 整个应用生命周期中，这行代码只会执行一次
        return new SingletonBean();
    }

    /**
     * 定义一个原型作用域的Bean
     *
     * @Scope("prototype") 或 @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
     * 表示该Bean的作用域为原型
     *
     * 【重要】：原型Bean在容器启动时不会实例化
     * 只有当调用 getBean() 或通过依赖注入获取时才会创建新实例
     * 而且每次获取都会创建新的实例
     *
     * @return PrototypeBean 实例
     */
    @Bean
    @Scope("prototype")  // 指定原型作用域
    public PrototypeBean prototypeBean() {
        // 当执行这行代码时，会打印"创建PrototypeBean实例"
        // 每次从容器获取prototypeBean()时，这行代码都会执行
        return new PrototypeBean();
    }
}
