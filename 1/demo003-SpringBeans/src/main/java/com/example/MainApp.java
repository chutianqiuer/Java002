package com.example;

import com.example.lifecycle.LifecycleConfig;
import com.example.lifecycle.Order;
import com.example.lifecycle.Product;
import com.example.postprocessor.MyBeanPostProcessor;
import com.example.postprocessor.PostProcessorConfig;
import com.example.scope.ScopeConfig;
import com.example.scope.SingletonBean;
import com.example.scope.PrototypeBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Spring Bean 演示主入口类
 *
 * 本程序演示Spring Bean的核心概念：
 * 1. Bean的作用域（singleton vs prototype）
 * 2. Bean的生命周期
 * 3. Bean的初始化回调（多种方式）
 * 4. Bean的销毁回调（多种方式）
 * 5. BeanPostProcessor的作用
 *
 * 【程序输出说明】
 *
 * 运行本程序，你会看到详细的输出，展示：
 * 1. 单例Bean和原型Bean的获取方式差异
 * 2. Bean生命周期的各个阶段
 * 3. BeanPostProcessor在初始化前后的处理
 * 4. 容器关闭时的销毁顺序
 */
public class MainApp {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("           Spring Bean 演示程序开始运行");
        System.out.println("=".repeat(70));

        // 创建Spring容器
        // AnnotationConfigApplicationContext: 基于注解配置的Spring应用上下文
        // 会自动扫描@Configuration注解的类，获取Bean定义
        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(
                ScopeConfig.class,        // 作用域配置
                LifecycleConfig.class,    // 生命周期配置
                PostProcessorConfig.class // BeanPostProcessor配置
            );

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    第一部分：Bean作用域演示");
        System.out.println("=".repeat(70));

        // ==================== 第一部分：Bean作用域演示 ====================
        // 演示singleton和prototype作用域的区别

        System.out.println("\n【演示1】获取单例Bean多次，比较是否是同一实例");
        System.out.println("-".repeat(50));

        // 两次获取singletonBean，应该是同一个实例（hashCode相同）
        SingletonBean singleton1 = context.getBean("singletonBean", SingletonBean.class);
        System.out.println("第一次获取 singletonBean: " + singleton1);

        SingletonBean singleton2 = context.getBean("singletonBean", SingletonBean.class);
        System.out.println("第二次获取 singletonBean: " + singleton2);

        // 验证：是否是同一个实例
        System.out.println("\n【验证】singleton1 == singleton2 ? " + (singleton1 == singleton2));
        System.out.println("【结论】单例Bean在容器中只有一个实例，多次获取返回同一个对象");

        System.out.println("\n【演示2】获取原型Bean多次，比较是否是不同实例");
        System.out.println("-".repeat(50));

        // 重置计数器
        PrototypeBean.resetInstanceCount();

        // 两次获取prototypeBean，应该是不同实例（hashCode不同）
        PrototypeBean prototype1 = context.getBean("prototypeBean", PrototypeBean.class);
        System.out.println("第一次获取 prototypeBean: " + prototype1);

        PrototypeBean prototype2 = context.getBean("prototypeBean", PrototypeBean.class);
        System.out.println("第二次获取 prototypeBean: " + prototype2);

        // 验证：是否是不同实例
        System.out.println("\n【验证】prototype1 == prototype2 ? " + (prototype1 != prototype2));
        System.out.println("【结论】原型Bean每次获取都会创建新实例");

        System.out.println("\n【演示3】单例Bean的实例变量是共享的");
        System.out.println("-".repeat(50));

        // 设置singleton1的名称
        singleton1.setName("单例Bean的共享数据");
        System.out.println("singleton1.setName('单例Bean的共享数据')");

        // 通过singleton2读取名称，应该是相同的值
        System.out.println("singleton2.getName() = " + singleton2.getName());
        System.out.println("【结论】单例Bean的实例变量是多线程共享的（线程不安全）");

        System.out.println("\n【演示4】原型Bean的实例变量是独立的");
        System.out.println("-".repeat(50));

        // 设置prototype1的名称
        prototype1.setName("Prototype1的数据");
        System.out.println("prototype1.setName('Prototype1的数据')");

        // prototype2的名称应该是null（未设置）
        System.out.println("prototype2.getName() = " + prototype2.getName());
        System.out.println("【结论】原型Bean的实例变量是相互独立的");

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    第二部分：Bean生命周期演示");
        System.out.println("=".repeat(70));

        // ==================== 第二部分：Bean生命周期演示 ====================
        // 演示Bean从创建到销毁的完整生命周期

        System.out.println("\n【演示5】获取Product Bean，观察初始化回调的执行顺序");
        System.out.println("-".repeat(50));

        // 获取Product Bean
        // 观察控制台输出，可以看到初始化回调的执行顺序：
        // 1. 构造方法
        // 2. 属性注入（setName, setPrice）
        // 3. BeanPostProcessor.postProcessBeforeInitialization()
        // 4. @PostConstruct注解的方法
        // 5. InitializingBean.afterPropertiesSet()
        // 6. 自定义的init-method
        // 7. BeanPostProcessor.postProcessAfterInitialization()
        Product product = context.getBean("product", Product.class);
        System.out.println("\nProduct Bean创建成功: " + product);

        System.out.println("\n【演示6】获取Order Bean，观察接口方式的初始化回调");
        System.out.println("-".repeat(50));

        Order order = context.getBean("order", Order.class);
        System.out.println("\nOrder Bean创建成功: " + order);

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    第三部分：BeanPostProcessor演示");
        System.out.println("=".repeat(70));

        // ==================== 第三部分：BeanPostProcessor演示 ====================
        // 演示BeanPostProcessor如何影响Bean的初始化过程

        System.out.println("\n【演示7】观察BeanPostProcessor的处理");
        System.out.println("-".repeat(50));
        System.out.println("BeanPostProcessor处理了 " + MyBeanPostProcessor.getCounter() + " 个Bean");

        System.out.println("\n【演示8】BeanPostProcessor的工作原理");
        System.out.println("-".repeat(50));
        System.out.println("BeanPostProcessor在每个Bean的初始化前后都会被调用：");
        System.out.println("- postProcessBeforeInitialization：在@PostConstruct、InitializingBean之前调用");
        System.out.println("- postProcessAfterInitialization：在所有初始化回调之后调用");
        System.out.println("\n【应用场景】");
        System.out.println("- Spring AOP使用BeanPostProcessor将Bean包装为代理对象");
        System.out.println("- @Autowired使用BeanPostProcessor实现依赖注入");
        System.out.println("- @Async使用BeanPostProcessor实现异步执行");

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    第四部分：容器关闭与Bean销毁演示");
        System.out.println("=".repeat(70));

        // ==================== 第四部分：容器关闭与Bean销毁演示 ====================
        // 演示容器关闭时Bean的销毁顺序

        System.out.println("\n【演示9】关闭Spring容器，观察销毁回调的执行顺序");
        System.out.println("-".repeat(50));
        System.out.println("正在关闭容器...");

        // 关闭容器
        // 这会触发singleton Bean的销毁回调
        // 销毁顺序（与初始化相反）：
        // 1. @PreDestroy注解的方法
        // 2. DisposableBean.destroy()
        // 3. 自定义的destroy-method
        context.close();

        System.out.println("\n【演示10】prototype Bean的销毁");
        System.out.println("-".repeat(50));
        System.out.println("【重要】prototype Bean的销毁不由Spring容器管理！");
        System.out.println("由于prototypeBean是原型作用域，容器不会自动调用其销毁方法");
        System.out.println("需要调用者自行管理prototype Bean的生命周期");

        System.out.println("\n" + "=".repeat(70));
        System.out.println("           Spring Bean 演示程序运行完成");
        System.out.println("=".repeat(70));

        // 打印总结
        System.out.println("\n【总结】");
        System.out.println("-".repeat(50));
        System.out.println("1. singleton Bean在容器中只有一个实例，由容器管理生命周期");
        System.out.println("2. prototype Bean每次获取都创建新实例，需要调用者自行管理销毁");
        System.out.println("3. 初始化回调：@PostConstruct > InitializingBean > init-method");
        System.out.println("4. 销毁回调：@PreDestroy > DisposableBean > destroy-method");
        System.out.println("5. BeanPostProcessor作用于所有Bean，可用于扩展功能（如AOP）");
    }
}
