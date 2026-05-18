package com.example.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 配置类 - 演示Spring的各种配置方式和使用场景
 *
 * 本类展示了：
 * 1. 如何使用@Component注解将类注册为Spring容器管理的Bean
 * 2. 如何使用@Value注解注入简单类型的配置值
 * 3. 如何通过Environment接口获取环境变量和配置属性
 * 4. 如何使用@Profile注解实现不同环境的Bean切换
 */
@Component
public class Config {

    /**
     * 成员变量：存储配置消息
     * 用于演示属性注入
     */
    private String message;

    /**
     * 成员变量：表示配置是否激活
     */
    private boolean active;

    /**
     * Spring的Environment接口实例
     * 用于访问应用程序环境和配置属性
     *
     * Environment接口继承了PropertyResolver接口，提供了：
     * - 获取激活的profile列表
     * - 获取配置属性的值
     * - 判断某个profile是否激活
     * - 解析属性占位符
     */
    private Environment environment;

    /**
     * 默认构造函数
     * Spring容器在实例化Bean时会调用此构造器
     */
    public Config() {
        this.message = "默认消息";
        this.active = false;
    }

    /**
     * 带参数的构造函数
     * 用于演示构造函数注入方式
     *
     * @param message 配置消息
     * @param active 是否激活
     */
    public Config(String message, boolean active) {
        this.message = message;
        this.active = active;
    }

    /**
     * 初始化方法
     * 演示Bean的生命周期回调
     * 该方法会在Bean的所有依赖注入完成后自动调用
     */
    public void init() {
        System.out.println("[Config] Bean初始化完成，执行init()方法");
        System.out.println("[Config] 当前消息: " + message);
        System.out.println("[Config] 当前激活状态: " + active);
    }

    /**
     * 清理方法
     * 演示Bean销毁前的回调
     * 该方法会在容器关闭时调用（仅对singleton作用域的Bean生效）
     */
    public void cleanup() {
        System.out.println("[Config] Bean即将被销毁，执行cleanup()方法");
    }

    // ==================== Getter和Setter方法 ====================

    /**
     * 获取配置消息
     * @return 当前配置的消息内容
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置配置消息
     * @param message 新的消息内容
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 判断配置是否激活
     * @return true表示已激活，false表示未激活
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 设置激活状态
     * @param active 新的激活状态
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * 设置Environment实例
     * 用于访问Spring环境和配置属性
     *
     * @param environment Spring容器注入的Environment实例
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 获取当前激活的Profile列表
     * Spring Profiles用于在不同环境（如dev、test、prod）下激活不同的Bean配置
     *
     * @return 激活的profile数组，如果没有任何profile激活则返回空数组
     */
    public String[] getActiveProfiles() {
        if (environment != null) {
            return environment.getActiveProfiles();
        }
        return new String[0];
    }

    /**
     * 获取默认的Profile列表
     * defaultProfiles是没有任何明确profile激活时使用的默认profile
     *
     * @return 默认的profile数组
     */
    public String[] getDefaultProfiles() {
        if (environment != null) {
            return environment.getDefaultProfiles();
        }
        return new String[0];
    }

    /**
     * 获取配置属性值
     * 通过Environment接口解析属性占位符或直接获取配置值
     *
     * @param key 属性键名
     * @return 属性值，如果不存在返回null
     */
    public String getProperty(String key) {
        if (environment != null) {
            return environment.getProperty(key);
        }
        return null;
    }

    /**
     * 获取配置属性值，并指定默认值
     * 当指定的属性不存在时，返回默认值
     *
     * @param key 属性键名
     * @param defaultValue 默认值
     * @return 属性值或默认值
     */
    public String getProperty(String key, String defaultValue) {
        if (environment != null) {
            return environment.getProperty(key, defaultValue);
        }
        return defaultValue;
    }

    /**
     * 判断某个profile是否已激活
     *
     * @param profile 要检查的profile名称
     * @return true表示该profile已激活，false表示未激活
     */
    public boolean isProfileActive(String profile) {
        if (environment != null) {
            return environment.acceptsProfiles(profile);
        }
        return false;
    }

    /**
     * 显示当前配置信息的便捷方法
     * 方便在调试时查看Bean的状态
     */
    public void displayInfo() {
        System.out.println("========== Config Bean 信息 ==========");
        System.out.println("消息: " + message);
        System.out.println("激活状态: " + active);
        if (environment != null) {
            System.out.println("激活的Profiles: " + String.join(", ", getActiveProfiles()));
            System.out.println("默认Profiles: " + String.join(", ", getDefaultProfiles()));
            System.out.println("Java版本: " + getProperty("java.version"));
            System.out.println("操作系统: " + getProperty("os.name"));
        }
        System.out.println("=====================================");
    }

    @Override
    public String toString() {
        return "Config{" +
                "message='" + message + '\'' +
                ", active=" + active +
                '}';
    }
}
