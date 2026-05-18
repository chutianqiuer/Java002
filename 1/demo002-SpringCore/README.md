# Spring Core IoC容器深入学习示例

## 项目简介

本项目是一个Spring Core（IoC容器）的学习示例，通过对比传统编程方式和Spring IoC方式，直观展示IoC和DI的核心概念。

## 目录结构

```
demo002-SpringCore/
├── pom.xml                                    # Maven项目配置文件
├── src/main/java/com/example/
│   ├── MainApp.java                          # 主入口程序
│   ├── ioc/
│   │   ├── TeaMaker.java                     # 传统方式示例：茶饮制作器
│   │   └── CoffeeMaker.java                   # 传统方式示例：咖啡制作器
│   ├── di/
│   │   ├── MessageService.java                # 消息服务接口
│   │   ├── EmailService.java                  # 邮件服务实现
│   │   ├── SmsService.java                    # 短信服务实现
│   │   └── Consumer.java                      # 消费者（演示注入方式）
│   └── config/
│       └── AppConfig.java                     # Java配置类
└── README.md                                  # 本文件
```

## 核心概念

### 什么是IoC（控制反转）？

IoC（Inversion of Control，控制反转）是一种软件设计原则。

**传统方式**：对象自己负责创建和管理它的依赖对象
```java
// 传统方式：对象自己new依赖
class TeaMaker {
    public void makeTea() {
        Water water = new Water();  // 自己创建依赖
        water.boil();
    }
}
```

**IoC方式**：对象的创建和依赖管理交给外部容器
```java
// IoC方式：依赖由外部注入
class TeaMaker {
    private Water water;
    public TeaMaker(Water water) {  // 依赖由容器注入
        this.water = water;
    }
}
```

### 什么是DI（依赖注入）？

DI（Dependency Injection，依赖注入）是IoC的一种具体实现方式，指通过构造函数、setter方法或字段将依赖对象注入到目标对象中。

**两种主要注入方式**：

1. **构造器注入（Constructor Injection）**
```java
public class Consumer {
    private final MessageService service;

    @Autowired
    public Consumer(MessageService service) {
        this.service = service;  // 通过构造器注入
    }
}
```

2. **Setter注入（Setter Injection）**
```java
public class Consumer {
    private MessageService service;

    @Autowired
    public void setService(MessageService service) {
        this.service = service;  // 通过setter方法注入
    }
}
```

### IoC vs DI 的区别

| 概念 | 说明 |
|------|------|
| IoC | 一种思想/原则，控制权的反转（从对象本身反转给容器） |
| DI | IoC的具体实现，通过注入方式提供依赖 |

简单理解：**IoC是思想，DI是实现方式**。

## 运行项目

### 前置条件

- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 编译项目

```bash
cd demo002-SpringCore
mvn clean compile
```

### 运行主程序

```bash
mvn exec:java -Dexec.mainClass="com.example.MainApp"
```

或先编译后运行：

```bash
mvn clean package
java -cp target/demo002-SpringCore-1.0-SNAPSHOT.jar com.example.MainApp
```

## 代码详解

### 第一部分：传统方式的耦合问题

`TeaMaker.java` 和 `CoffeeMaker.java` 演示了传统方式的问题：

- 对象自己创建依赖（`new Water()`）
- 高度耦合，难以测试
- 依赖关系不清晰
- 对象管理混乱

### 第二部分：Spring IoC容器演示

`AppConfig.java` 使用Java配置类替代XML：

```java
@Configuration
public class AppConfig {
    @Bean
    public MessageService emailService() {
        return new EmailService();
    }
}
```

### 第三部分：依赖注入演示

`Consumer.java` 演示两种注入方式：

- **构造器注入**：通过构造函数注入必需依赖
- **Setter注入**：通过setter方法注入可选依赖

### 第四部分：Bean的作用域

Spring容器中的Bean默认是**单例模式**，整个容器中只有一个实例。

## 关键注解说明

| 注解 | 说明 |
|------|------|
| `@Configuration` | 标注配置类，替代XML配置 |
| `@Bean` | 标注方法，返回一个Spring Bean |
| `@Autowired` | 自动注入依赖 |
| `@Primary` | 标注为主要实现，优先使用 |
| `@Component` | 标注组件，自动扫描时使用 |

## 输出示例

```
========================================
Spring Core IoC容器深入学习示例
========================================

【第一部分】传统方式的耦合问题演示
----------------------------------------
Water: 把水烧开...
【传统方式】TeaMaker: 用普通自来水制作了一杯茶

Water: 把水烧开...
【传统方式】CoffeeMaker: 用普通自来水制作了一杯咖啡

========================================

【第二部分】Spring IoC容器演示
----------------------------------------
【@Bean】创建EmailService实例
【@Bean】创建SmsService实例
【@Bean】创建Consumer实例（使用构造器注入）
【@Bean】注入的MessageService类型: EmailService
Spring IoC容器创建成功！
...
```

## 学习要点

1. **理解IoC思想**：将对象创建和依赖管理交给容器
2. **掌握DI实现**：构造器注入（推荐）和Setter注入
3. **熟悉配置方式**：@Configuration + @Bean 替代 XML
4. **理解Bean生命周期**：单例作用域是默认行为

## 扩展学习

- `@Component` + `@ComponentScan` 自动扫描
- `@Qualifier` 指定具体Bean
- `@Scope` 设置Bean作用域
- `@Lazy` 懒加载
- `@PostConstruct` / `@PreDestroy` 生命周期回调
