package com.example.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Order类 - 演示通过实现接口方式进行生命周期回调
 *
 * 【对比：注解方式 vs 接口方式】
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                           注解方式（推荐）                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 使用@PostConstruct和@PreDestroy注解                                      │
 * │ 优点：                                                                    │
 * │   • 代码清晰，注解一目了然                                                 │
 * │   • 不耦合Spring特定接口                                                  │
 * │   • 是JSR-250标准，Java EE规范的一部分                                    │
 * │   • 可以在任何POJO上使用                                                  │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 需要额外的依赖（jakarta.annotation-api）                              │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                           接口方式                                        │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │ 实现InitializingBean和DisposableBean接口                                 │
 * │ 优点：                                                                    │
 * │   • Spring原生接口，兼容性最好                                            │
 * │   • 不需要额外的依赖                                                      │
 * │                                                                          │
 * │ 缺点：                                                                    │
 * │   • 耦合Spring特定接口，更换容器时需要修改代码                             │
 * │   • 必须实现所有方法，即使为空                                            │
 * │   • Java不支持多继承，如果Bean需要继承其他类则无法使用                     │
 * │                                                                          │
 * │ 【建议】：除非有特殊原因，否则优先使用注解方式                             │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * 【总结】
 *
 * 实际开发中推荐使用@PostConstruct和@PreDestroy注解方式，因为：
 * 1. 代码更清晰，注解语义明确
 * 2. 不耦合Spring，迁移性好
 * 3. 是Java标准规范
 *
 * 但了解接口方式也很重要，因为：
 * 1. 有些遗留代码可能使用接口方式
 * 2. 可以深入理解Spring的底层实现
 * 3. 在某些高级场景下可能需要直接实现接口
 */
public class Order implements InitializingBean, DisposableBean {

    private String orderId;
    private String customerName;

    /**
     * 默认构造方法
     */
    public Order() {
        System.out.println("【Order构造方法】创建Order实例 - " + this.hashCode());
    }

    /**
     * 设置订单ID
     */
    public void setOrderId(String orderId) {
        System.out.println("【Order.setOrderId】设置订单ID: " + orderId);
        this.orderId = orderId;
    }

    /**
     * 设置客户名称
     */
    public void setCustomerName(String customerName) {
        System.out.println("【Order.setCustomerName】设置客户名称: " + customerName);
        this.customerName = customerName;
    }

    /**
     * 获取订单ID
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 获取客户名称
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 初始化方法 - 实现InitializingBean接口
     *
     * 【执行时机】：在所有Bean属性注入完成后调用
     *
     * 【注意事项】：
     * 1. 这个方法会自动被Spring调用，不需要手动调用
     * 2. 如果抛出异常，Bean的创建会失败
     * 3. 这个方法可以访问Bean的属性，进行验证或转换
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("【Order.afterPropertiesSet】InitializingBean接口初始化方法被调用");
        System.out.println("【Order.afterPropertiesSet】订单ID: " + orderId + ", 客户名称: " + customerName);

        // 可以在这个方法中进行订单验证等操作
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        System.out.println("【Order.afterPropertiesSet】InitializingBean初始化完成");
    }

    /**
     * 销毁方法 - 实现DisposableBean接口
     *
     * 【执行时机】：在Bean被销毁前调用
     *
     * 【注意事项】：
     * 1. 这个方法会自动被Spring调用（仅对singleton Bean有效）
     * 2. prototype Bean不会自动调用这个方法
     * 3. 在这个方法中应该释放Bean持有的资源
     */
    @Override
    public void destroy() throws Exception {
        System.out.println("【Order.destroy】DisposableBean接口销毁方法被调用");
        System.out.println("【Order.destroy】正在关闭订单: " + orderId);
        // 可以在这个方法中关闭数据库连接、释放文件句柄等
        System.out.println("【Order.destroy】DisposableBean销毁完成");
    }

    /**
     * toString方法
     */
    @Override
    public String toString() {
        return "Order{" +
                "hashCode=" + this.hashCode() +
                ", orderId='" + orderId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
