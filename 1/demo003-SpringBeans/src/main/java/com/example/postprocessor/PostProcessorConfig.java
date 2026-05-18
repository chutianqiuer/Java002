package com.example.postprocessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BeanPostProcessor配置类
 *
 * 本类演示如何注册BeanPostProcessor
 *
 * 【注意事项】
 *
 * 1. BeanPostProcessor需要通过@Bean方法注册到容器中
 * 2. BeanPostProcessor会自动应用于容器中所有的Bean
 * 3. 如果只需要应用于特定Bean，可以在postProcessBeforeInitialization中判断beanName
 * 4. BeanPostProcessor的注册顺序很重要，先注册的会先执行
 */
@Configuration
public class PostProcessorConfig {

    /**
     * 注册MyBeanPostProcessor
     *
     * 这个BeanPostProcessor会应用于容器中所有的Bean
     * 在每个Bean的初始化前后都会被调用
     *
     * @return MyBeanPostProcessor实例
     */
    @Bean
    public MyBeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
