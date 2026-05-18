# demo003-SpringBeans - Spring Bean管理深入学习示例

本项目演示Spring Framework中Bean管理的核心概念，包括Bean作用域、生命周期、初始化回调、销毁回调以及BeanPostProcessor后置处理器。

## 项目结构

```
demo003-SpringBeans/
├── pom.xml                                      # Maven配置文件
├── src/main/java/com/example/
│   ├── MainApp.java                            # 主入口程序
│   ├── scope/
│   │   ├── ScopeConfig.java                    # Bean作用域配置
│   │   ├── SingletonBean.java                  # 单例作用域示例
│   │   └── PrototypeBean.java                  # 原型作用域示例
│   ├── lifecycle/
│   │   ├── LifecycleConfig.java                # 生命周期配置
│   │   ├── Product.java                        # 生命周期演示（Product类）
│   │   └── Order.java                          # 生命周期演示（Order类）
│   └── postprocessor/
│       ├── PostProcessorConfig.java           # BeanPostProcessor配置
│       └── MyBeanPostProcessor.java           # 自定义后置处理器
└── README.md                                   # 本文件
```

## 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven 3.6+

### 编译项目

```bash
cd demo003-SpringBeans
mvn clean compile
```

### 运行演示

```bash
mvn exec:java -Dexec.mainClass="com.example.MainApp"
```

或者先打包后运行：

```bash
mvn clean package
java -jar target/demo003-SpringBeans-1.0-SNAPSHOT.jar
```

## 核心概念

### 1. Bean作用域

Spring支持多种Bean作用域，最常用的是`singleton`和`prototype`：

| 作用域 | 说明 | 使用场景 |
|--------|------|----------|
| singleton | 容器中只有一个实例 | 无状态Bean、共享Bean |
| prototype | 每次获取创建新实例 | 有状态Bean、需要独立实例 |

**代码演示位置**：`com.example.scope`

```java
// 单例Bean - 容器中只有一个实例
@Bean
@Scope("singleton")
public SingletonBean singletonBean() {
    return new SingletonBean();
}

// 原型Bean - 每次获取都创建新实例
@Bean
@Scope("prototype")
public PrototypeBean prototypeBean() {
    return new PrototypeBean();
}
```

### 2. Bean生命周期

Bean从创建到销毁经历以下阶段：

```
构造方法 → 属性注入 → 初始化 → 使用 → 销毁
```

**代码演示位置**：`com.example.lifecycle`

### 3. 初始化回调方式

Spring提供了三种初始化回调方式：

| 方式 | 说明 | 推荐度 |
|------|------|--------|
| @PostConstruct | JSR-250注解 | ⭐⭐⭐ 强烈推荐 |
| InitializingBean接口 | 实现接口方法 | ⭐⭐ |
| init-method | @Bean属性配置 | ⭐⭐⭐ 推荐 |

```java
// 方式1：@PostConstruct注解（推荐）
@PostConstruct
public void init() {
    System.out.println("@PostConstruct初始化");
}

// 方式2：实现InitializingBean接口
@Override
public void afterPropertiesSet() throws Exception {
    System.out.println("InitializingBean初始化");
}

// 方式3：@Bean的initMethod属性
@Bean(initMethod = "customInit")
public MyBean myBean() {
    return new MyBean();
}
```

### 4. 销毁回调方式

| 方式 | 说明 | 推荐度 |
|------|------|--------|
| @PreDestroy | JSR-250注解 | ⭐⭐⭐ 强烈推荐 |
| DisposableBean接口 | 实现接口方法 | ⭐⭐ |
| destroy-method | @Bean属性配置 | ⭐⭐⭐ 推荐 |

```java
// 方式1：@PreDestroy注解（推荐）
@PreDestroy
public void cleanup() {
    System.out.println("@PreDestroy销毁");
}

// 方式2：实现DisposableBean接口
@Override
public void destroy() throws Exception {
    System.out.println("DisposableBean销毁");
}

// 方式3：@Bean的destroyMethod属性
@Bean(destroyMethod = "customDestroy")
public MyBean myBean() {
    return new MyBean();
}
```

### 5. BeanPostProcessor

BeanPostProcessor是Spring的扩展接口，允许在Bean初始化前后进行额外处理：

```java
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前: " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后: " + beanName);
        return bean;
    }
}
```

**应用场景**：
- Spring AOP（将Bean包装为代理对象）
- @Autowired注解处理
- @Async注解处理

## 执行顺序总结

Bean初始化时的执行顺序：

```
1. 构造方法
2. 属性注入
3. BeanPostProcessor.postProcessBeforeInitialization()
4. @PostConstruct注解的方法
5. InitializingBean.afterPropertiesSet()
6. 自定义的init-method
7. BeanPostProcessor.postProcessAfterInitialization()
```

Bean销毁时的执行顺序（与初始化相反）：

```
1. @PreDestroy注解的方法
2. DisposableBean.destroy()
3. 自定义的destroy-method
```

## 注意事项

1. **prototype Bean的销毁**：Spring容器不管理prototype Bean的销毁，需要调用者自行管理。

2. **线程安全**：singleton Bean的实例变量在多线程环境下是共享的，需要自行处理线程安全问题。

3. **接口耦合**：实现InitializingBean和DisposableBean接口会耦合Spring，不推荐在业务代码中使用。

4. **注解方式优先**：推荐使用@PostConstruct和@PreDestroy注解，代码更清晰，不耦合Spring。

## 延伸学习

- Spring AOP：通过BeanPostProcessor实现，了解代理模式
- BeanFactoryPostProcessor：修改Bean定义的扩展点
- 自定义Scope：创建自定义作用域
- Spring Event：使用BeanPostProcessor实现事件机制
