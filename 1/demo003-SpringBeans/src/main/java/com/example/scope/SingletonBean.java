package com.example.scope;

/**
 * 单例作用域Bean示例
 *
 * 【单例Bean的特点】
 *
 * 1. 唯一实例：整个Spring容器中只存在一个实例
 * 2. 容器管理：由Spring容器负责实例的创建、管理和销毁
 * 3. 延迟初始化（可选）：可以使用@Lazy注解延迟初始化
 * 4. 线程共享：该实例在多线程环境下是共享的，需要注意线程安全
 *
 * 【使用场景】
 * - 适用于无状态的对象（不存储实例变量）
 * - 适用于需要共享数据的场景
 * - 适用于Service层、DAO层、工具类等
 *
 * 【注意事项】
 * - 单例Bean不是线程安全的，如果Bean有实例变量，需要自行处理线程安全问题
 * - 单例Bean默认在容器启动时创建，可以通过@Lazy延迟初始化
 *
 * 【代码演示】
 * 在MainApp中会演示：
 * 1. 单例Bean多次获取返回的是同一个实例
 * 2. 单例Bean的实例变量是共享的
 */
public class SingletonBean {

    /**
     * 实例变量 - 用于演示单例Bean的共享特性
     * 【警告】：这个变量在多线程环境下是不安全的
     */
    private String name;

    /**
     * 构造方法
     * 【注意】：单例Bean的构造方法会在容器启动时（默认）或首次使用时调用
     */
    public SingletonBean() {
        System.out.println("【SingletonBean构造方法】创建了SingletonBean实例 - " + this.hashCode());
    }

    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * 【线程安全隐患】：多个线程同时调用可能产生竞态条件
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * toString方法
     */
    @Override
    public String toString() {
        return "SingletonBean{" +
                "hashCode=" + this.hashCode() +
                ", name='" + name + '\'' +
                '}';
    }
}
