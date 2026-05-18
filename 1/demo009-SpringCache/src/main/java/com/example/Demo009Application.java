package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Spring Cache 演示主应用程序
 *
 * 【Spring Cache核心思想】
 *
 * Spring Cache是Spring框架提供的缓存抽象层，它本身不直接实现缓存功能，
 * 而是通过统一的API和注解，让开发者可以方便地切换不同的缓存实现（如EhCache、Caffeine、Redis等）。
 *
 * 核心设计原则："不直接操作具体缓存实现"
 * - 开发者使用@Cacheable、@CacheEvict等注解声明缓存操作
 * - Spring在运行时根据配置自动选择合适的缓存实现
 * - 切换缓存实现只需修改配置，无需修改业务代码
 *
 * 【Spring Cache解决的问题】
 * 1. 硬编码问题：避免在业务代码中直接调用缓存API
 * 2. 缓存实现切换：同一套注解，支持多种缓存实现
 * 3. 缓存一致性：通过声明式方式管理缓存生命周期
 *
 * 【本示例演示内容】
 * 1. @Cacheable：缓存方法返回结果
 * 2. @CacheEvict：清除缓存
 * 3. @CachePut：更新缓存
 * 4. @Caching：组合多个缓存操作
 * 5. @CacheConfig：类级别公共缓存配置
 * 6. SpEL表达式在缓存注解中的应用
 * 7. 自定义CacheManager
 * 8. 多级缓存实现
 *
 * 【@EnableCaching注解作用】
 * - 启用Spring的注释驱动的缓存管理功能
 * - 让Spring自动处理带有@Cacheable、@CacheEvict等注解的方法
 * - 必须在配置类或主启动类上添加此注解
 */
@SpringBootApplication
@EnableCaching
public class Demo009Application {

    /**
     * 应用程序入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Spring Cache 缓存抽象演示项目启动");
        System.out.println("========================================");
        System.out.println();
        System.out.println("【Spring Cache核心概念】");
        System.out.println("Spring Cache通过统一的API和注解，让开发者可以方便地切换不同的缓存实现。");
        System.out.println("业务代码只需关注缓存操作语义（缓存、清除、更新），无需关心具体缓存实现。");
        System.out.println();

        // 启动Spring Boot应用程序
        SpringApplication.run(Demo009Application.class, args);

        System.out.println();
        System.out.println("【项目结构】");
        System.out.println("- Demo009Application：主启动类");
        System.out.println("- CacheConfig：缓存配置类（配置EhCache/Caffeine）");
        System.out.println("- UserService：演示@Cacheable、@CacheEvict等注解");
        System.out.println("- ProductService：演示多级缓存实现");
        System.out.println("- MyCacheManager：自定义CacheManager实现");
        System.out.println("========================================");
    }
}
