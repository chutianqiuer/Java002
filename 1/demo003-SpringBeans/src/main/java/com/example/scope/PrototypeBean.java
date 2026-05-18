package com.example.scope;

/**
 * 原型作用域Bean示例
 *
 * 【原型Bean的特点】
 *
 * 1. 每次获取都创建新实例：每次调用getBean()或依赖注入都会创建新的对象
 * 2. 容器不管理生命周期：Spring容器只负责创建，不负责销毁，需要调用者自行管理
 * 3. 延迟创建：Bean在首次请求时才会被实例化
 * 4. 独立实例：每个实例都有独立的实例变量，互不干扰
 *
 * 【使用场景】
 * - 适用于有状态的对象（需要保持各自独立的状态）
 * - 适用于需要为每个请求创建独立实例的场景
 * - 适用于Web中的Controller、Action等
 * - 适用于需要频繁创建和销毁的对象
 *
 * 【注意事项】
 * - 原型Bean每次都创建新实例，会有一定的性能开销
 * - Spring容器不负责原型Bean的销毁，需要调用者自行管理
 * - 可以使用@PreDestroy标注方法来监听销毁（但不会被自动调用）
 *
 * 【代码演示】
 * 在MainApp中会演示：
 * 1. 原型Bean每次获取都返回不同的实例
 * 2. 原型Bean的实例变量是独立的，不会相互影响
 *
 * 【与单例Bean的对比】
 *
 * ┌──────────────────┬───────────────────┬───────────────────┐
 * │     特性          │   singleton        │   prototype       │
 * ├──────────────────┼───────────────────┼───────────────────┤
 * │ 实例数量          │ 容器中唯一实例      │ 每次获取都创建新实例│
 * │ 创建时机          │ 容器启动时          │ 首次请求时         │
 * │ 销毁管理          │ 容器管理            │ 调用者管理         │
 * │ 性能              │ 更快（无需创建）    │ 稍慢（每次创建）   │
 * │ 内存              │ 占用少              │ 占用多             │
 * │ 线程安全          │ 需要自行处理        │ 天然线程安全       │
 * └──────────────────┴───────────────────┴───────────────────┘
 */
public class PrototypeBean {

    /**
     * 实例变量 - 用于演示原型Bean的独立特性
     * 每个实例都有自己独立的name，互不干扰
     */
    private String name;

    /**
     * 静态计数器 - 记录创建了多少个原型Bean实例
     * 用于演示原型Bean每次都创建新实例
     */
    private static int instanceCount = 0;

    /**
     * 构造方法
     * 【重要】：每次调用构造方法都表示创建了一个新的实例
     */
    public PrototypeBean() {
        instanceCount++;
        System.out.println("【PrototypeBean构造方法】创建了PrototypeBean实例 #" + instanceCount +
                           " - hashCode: " + this.hashCode());
    }

    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * 由于每个Bean实例都是独立的，所以设置name不会影响其他实例
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取当前创建的实例数量
     */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /**
     * 重置计数器（主要用于测试）
     */
    public static void resetInstanceCount() {
        instanceCount = 0;
    }

    /**
     * toString方法
     */
    @Override
    public String toString() {
        return "PrototypeBean{" +
                "hashCode=" + this.hashCode() +
                ", name='" + name + '\'' +
                '}';
    }
}
