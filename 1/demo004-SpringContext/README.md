# Spring Context（应用上下文）深入学习示例

本项目是Spring Context（应用上下文）的深入学习示例，展示了Spring容器中ApplicationContext的多种实现类、环境配置、资源加载等核心功能。

## 项目概述

Spring Context是Spring框架的核心模块之一，提供了IoC（控制反转）和DI（依赖注入）功能。ApplicationContext是BeanFactory的子接口，提供了更丰富的企业级功能。

### 核心功能演示

1. **ApplicationContext常见实现类及适用场景**
2. **ClassPathXmlApplicationContext vs FileSystemXmlApplicationContext**
3. **@Configuration和@ComponentScan的使用**
4. **Environment接口：获取profile、属性值**
5. **@Profile注解：不同环境激活不同Bean**
6. **ResourceLoader：加载文件等资源**

## 项目结构

```
demo004-SpringContext/
├── pom.xml                                    # Maven项目配置文件
├── src/main/java/com/example/
│   ├── MainApp.java                           # 主入口类
│   ├── context/                               # ApplicationContext实现类示例
│   │   ├── ClassPathXmlApplicationContextDemo.java
│   │   ├── FileSystemXmlApplicationContextDemo.java
│   │   ├── AnnotationConfigApplicationContextDemo.java
│   │   └── WebApplicationContextDemo.java
│   ├── env/                                   # 环境配置示例
│   │   ├── EnvironmentDemo.java               # Environment接口演示
│   │   └── ProfileDemo.java                   # @Profile注解演示
│   ├── resource/                              # 资源加载示例
│   │   └── ResourceLoaderDemo.java
│   └── model/                                 # 模型类
│       └── Config.java
├── src/main/resources/
│   ├── beans.xml                              # XML配置示例
│   └── application.properties                 # 属性配置文件
└── README.md
```

## 环境要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本

## 快速开始

### 1. 编译项目

```bash
cd demo004-SpringContext
mvn clean compile
```

### 2. 运行主程序

```bash
mvn exec:java
```

或使用以下命令直接运行：

```bash
mvn clean compile exec:java
```

### 3. 运行测试

```bash
mvn test
```

## 主要演示内容

### 1. ClassPathXmlApplicationContext

从classpath（类路径）中加载XML配置文件创建Spring容器。

**适用场景**：
- 配置文件在src/main/resources目录下
- 配置文件需要随应用一起打包
- 不需要灵活变更配置文件位置

**创建方式**：
```java
ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
Config config = context.getBean("xmlConfigBean", Config.class);
```

### 2. FileSystemXmlApplicationContext

从文件系统路径加载XML配置文件创建Spring容器。

**适用场景**：
- 配置文件放在文件系统任意位置
- 需要在运行时动态变更配置
- 配置文件独立于应用部署

**路径格式**：
```java
// 绝对路径
ApplicationContext context = new FileSystemXmlApplicationContext("/opt/spring/beans.xml");

// 相对路径（相对于当前工作目录）
ApplicationContext context = new FileSystemXmlApplicationContext("src/main/resources/beans.xml");

// 使用classpath前缀
ApplicationContext context = new FileSystemXmlApplicationContext("classpath:beans.xml");
```

### 3. AnnotationConfigApplicationContext

从注解配置类加载配置创建Spring容器。

**适用场景**：
- 使用Java配置类代替XML
- 使用注解定义Bean
- 需要类型安全的配置

**创建方式**：
```java
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
context.register(Config.class);
context.refresh();
```

### 4. WebApplicationContext

专门为Web应用设计的应用上下文，支持request、session等Web作用域。

**常见实现类**：
- XmlWebApplicationContext：XML配置的Web应用
- AnnotationConfigWebApplicationContext：注解配置的Web应用

**获取方式**：
```java
@Autowired
private WebApplicationContext context;

// 或通过ServletContext获取
WebApplicationContext context = WebApplicationContextUtils
    .getWebApplicationContext(servletContext);
```

### 5. Environment接口

访问应用程序环境和配置属性。

**主要功能**：
- 获取激活的Profile
- 获取配置属性值
- 判断Profile是否激活
- 解析属性占位符

**使用示例**：
```java
ConfigurableEnvironment env = context.getEnvironment();

// 获取激活的Profile
String[] activeProfiles = env.getActiveProfiles();

// 获取配置属性
String appName = env.getProperty("app.name");
String appNameWithDefault = env.getProperty("app.name", "默认名称");

// 判断Profile是否激活
boolean isDev = env.acceptsProfiles("dev");
```

### 6. @Profile注解

根据不同环境激活不同的Bean配置。

**激活Profile的方式**：

```bash
# 命令行参数
java -jar app.jar --spring.profiles.active=dev

# JVM系统属性
java -Dspring.profiles.active=dev -jar app.jar

# 环境变量
export SPRING_PROFILES_ACTIVE=dev
```

**使用示例**：
```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new H2DataSource();
    }

    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        return new MySQLDataSource();
    }
}
```

### 7. ResourceLoader

加载各种类型的资源（文件、类路径资源、URL资源等）。

**使用示例**：
```java
ResourceLoader loader = context;

// 加载classpath资源
Resource resource1 = loader.getResource("classpath:application.properties");

// 加载文件系统资源
Resource resource2 = loader.getResource("file:./config.xml");

// 读取资源内容
InputStream inputStream = resource1.getInputStream();
String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
```

## 常见问题

### Q: ClassPathXmlApplicationContext和FileSystemXmlApplicationContext有什么区别？

**A**: 主要区别在于配置文件的加载位置：
- `ClassPathXmlApplicationContext`从classpath加载，配置文件必须在类路径中
- `FileSystemXmlApplicationContext`从文件系统加载，配置文件可以在任意位置

### Q: 如何选择使用哪种ApplicationContext？

**A**: 选择依据：
- 配置文件在classpath中 → ClassPathXmlApplicationContext
- 配置文件在文件系统任意位置 → FileSystemXmlApplicationContext
- 使用Java配置类和注解 → AnnotationConfigApplicationContext
- Web应用 → XmlWebApplicationContext或AnnotationConfigWebApplicationContext

### Q: 如何激活特定的Profile？

**A**: 有以下几种方式：
1. 编程式：`context.getEnvironment().setActiveProfiles("dev")`
2. 命令行：`--spring.profiles.active=dev`
3. 环境变量：`SPRING_PROFILES_ACTIVE=dev`

### Q: @Profile和@Conditional有什么区别？

**A**: @Profile基于环境（Profile）条件判断，而@Conditional更通用，支持任意条件判断。

## 相关资源

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Spring Context Module](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans)
- [ApplicationContext Documentation](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationContext.html)

## 许可证

本项目仅用于学习交流，使用MIT许可证。
