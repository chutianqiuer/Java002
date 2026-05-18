package com.example.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * MyBeanPostProcessor - 演示Spring的BeanPostProcessor后置处理器
 *
 * 【BeanPostProcessor的作用】
 *
 * BeanPostProcessor是Spring框架提供的一个扩展接口
 * 它允许我们在Bean的初始化前后添加自定义的逻辑，进行额外的处理
 *
 * 【工作原理】
 *
 * Spring容器中的所有Bean在初始化过程中，都会经过BeanPostProcessor的处理：
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                           Bean生命周期流程                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                          │
 * │  1. Bean实例化（调用构造方法）                                            │
 * │         ↓                                                                │
 * │  2. 属性注入（setter注入）                                               │
 * │         ↓                                                                │
 * │  3. BeanPostProcessor.postProcessBeforeInitialization()  ← 可以这里干预 │
 * │         ↓                                                                │
 * │  4. @PostConstruct注解的方法                                             │
 * │         ↓                                                                │
 * │  5. InitializingBean.afterPropertiesSet()                                │
 * │         ↓                                                                │
 * │  6. 自定义的init-method                                                  │
 * │         ↓                                                                │
 * │  7. BeanPostProcessor.postProcessAfterInitialization()   ← 可以这里干预 │
 * │         ↓                                                                │
 * │  8. Bean准备就绪，可以使用了                                              │
 * │         ↓                                                                │
 * │  ... 使用中 ...                                                          │
 * │         ↓                                                                │
 * │  9. @PreDestroy注解的方法                                                │
 * │         ↓                                                                │
 * │ 10. DisposableBean.destroy()                                            │
 * │         ↓                                                                │
 * │ 11. 自定义的destroy-method                                               │
 * │                                                                          │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * 【使用场景】
 *
 * 1. 属性验证：在Bean初始化前检查必要属性是否都已注入
 * 2. 属性转换：在Bean初始化前/后对属性进行转换或增强
 * 3. 代理包装：将原始Bean包装为代理对象（Spring AOP就是基于此实现）
 * 4. 日志记录：在Bean初始化前后打印日志，记录Bean的创建和销毁
 * 5. 条件创建：根据条件决定是否创建某个Bean
 *
 * 【注意事项】
 *
 * 1. BeanPostProcessor作用于容器中所有的Bean
 * 2. 如果只需要处理特定Bean，可以在方法中通过beanName进行判断
 * 3. BeanPostProcessor是针对Bean实例的，不是针对Bean定义的
 * 4. 如果postProcessBeforeInitialization返回null，后续处理会中断
 * 5. BeanPostProcessor是接口，需要实现两个方法：
 *    - postProcessBeforeInitialization：在初始化之前调用
 *    - postProcessAfterInitialization：在初始化之后调用
 *
 * 【实际应用】
 *
 * Spring框架内部大量使用BeanPostProcessor：
 * -AutowiredAnnotationBeanPostProcessor：处理@Autowired注解
 * -CommonAnnotationBeanPostProcessor：处理@PostConstruct、@PreDestroy等
 * -RequiredAnnotationBeanPostProcessor：处理@Required注解
 * -AsyncAnnotationBeanPostProcessor：处理@Async注解
 * -ScheduledAnnotationBeanPostProcessor：处理@Scheduled注解
 */
public class MyBeanPostProcessor implements BeanPostProcessor {

    /**
     * 计数器：记录处理了多少个Bean
     */
    private static int counter = 0;

    /**
     * 构造方法
     */
    public MyBeanPostProcessor() {
        System.out.println("【MyBeanPostProcessor构造方法】BeanPostProcessor被创建");
    }

    /**
     * 在Bean初始化之前调用
     *
     * 【参数说明】
     * - bean：原始Bean实例
     * - beanName：Bean在容器中的名称
     *
     * 【返回值】
     * - 通常返回原始的bean
     * - 如果返回null，后续的初始化步骤不会执行
     * - 可以返回一个新的Bean实例来替代原始Bean
     *
     * 【使用场景】
     * - 修改Bean的属性
     * - 包装Bean为代理对象
     * - 根据条件决定是否继续初始化
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        counter++;
        System.out.println("【BeanPostProcessor.postProcessBeforeInitialization】#"
                           + counter + " 处理Bean: " + beanName
                           + " - 类型: " + bean.getClass().getSimpleName());

        // 可以在此处对Bean进行额外的处理
        // 例如：验证属性、日志记录、属性转换等

        // 返回原始Bean，继续后续的初始化流程
        return bean;
    }

    /**
     * 在Bean初始化之后调用
     *
     * 【参数说明】
     * - bean：已经过初始化处理的Bean实例
     * - beanName：Bean在容器中的名称
     *
     * 【返回值】
     * - 通常返回原始的bean
     * - 如果返回null，该Bean将不可用
     * - 可以返回一个新的Bean实例来替代原始Bean（通常是代理对象）
     *
     * 【使用场景】
     * - 将Bean包装为代理对象（Spring AOP的做法）
     * - 对Bean进行最终的增强
     * - 返回完全不同的对象
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("【BeanPostProcessor.postProcessAfterInitialization】#"
                           + counter + " 处理Bean: " + beanName
                           + " - 类型: " + bean.getClass().getSimpleName()
                           + " - hashCode: " + bean.hashCode());

        // 可以在此处对Bean进行额外的处理
        // 常见的做法是将Bean包装为代理对象

        // 返回原始Bean，继续后续的使用
        return bean;
    }

    /**
     * 获取已处理的Bean数量
     */
    public static int getCounter() {
        return counter;
    }
}
