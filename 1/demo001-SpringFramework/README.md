# Spring Framework 基础入门示例

这是一个演示 Spring Framework 最核心功能（IoC 容器和依赖注入）的入门示例。

## 项目结构

```
demo001-SpringFramework/
├── pom.xml                              # Maven 项目配置文件
├── src/main/java/com/example/
│   ├── MainApp.java                      # 主入口类，演示传统方式和 Spring 方式
│   ├── AppConfig.java                    # Spring 配置类
│   └── model/
│       └── User.java                     # 用户实体类
└── README.md                             # 本文件
```

## 运行项目

### 前置条件

1. **安装 JDK**
   - 本项目需要 JDK 8 或更高版本
   - 检查方法：在命令行运行 `java -version`

2. **安装 Maven**
   - 本项目使用 Maven 构建
   - 检查方法：在命令行运行 `mvn -version`

### 运行步骤

1. **进入项目目录**
   ```bash
   cd demo001-SpringFramework
   ```

2. **使用 Maven 编译项目**
   ```bash
   mvn compile
   ```
   这会自动下载依赖（spring-context）并编译 Java 源代码。

3. **运行主程序**
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.MainApp"
   ```

   或者直接运行（编译后）：
   ```bash
   java -cp target/classes com.example.MainApp
   ```

### 预期输出

运行成功后，你应该能看到类似以下输出：

```
========================================
Spring Framework 基础示例演示
========================================

【第一部分】传统方式创建对象（不使用 Spring）
----------------------------------------
使用传统方式创建了第一个用户：User{id=1, name='张三', email='zhangsan@example.com'}
使用传统方式创建了第二个用户：User{id=2, name='李四', email='lisi@example.com'}
用户信息：id=1, name=张三, email=zhangsan@example.com

【传统方式的问题】
1. 对象太多了要自己 new，代码重复
2. 对象之间的依赖关系自己管理，容易出错
3. 要修改对象的创建方式，所有用到的地方都要改
4. 很难进行单元测试（因为对象是自己创建的，不好替换）

【第二部分】使用 Spring IoC 容器获取 Bean
----------------------------------------
步骤 1: 创建 Spring IoC 容器...
Spring IoC 容器创建成功！

步骤 2: 从容器中获取 Bean...
从 Spring 容器获取的用户：User{id=100, name='Spring管理的User', email='spring@example.com'}

步骤 3: 验证 Spring 容器的 Bean 管理特性...
第二次获取的 Bean：User{id=100, name='Spring管理的User', email='spring@example.com'}
两次获取的是否是同一个对象？true

【结论】
由于 User Bean 的作用域是 singleton（单例），
所以每次 getBean("user") 都返回同一个对象实例。
...

========================================
【总结】传统方式 vs Spring 方式
========================================
...
```

## 核心概念说明

### 什么是 IoC（控制反转）？

**控制反转**是一种设计原则，它将对象的创建和依赖管理的控制权从应用程序代码转移到外部容器（这里是 Spring IoC 容器）。

**对比理解**：
- **传统方式**：应用程序代码自己 `new` 创建对象，像自己做饭
- **IoC 方式**：把对象创建交给 Spring 容器，像去餐厅吃饭

### 什么是依赖注入（DI）？

依赖注入是实现 IoC 的一种方式。容器自动将依赖的对象注入到需要的地方。

### 什么是 Bean？

在 Spring 中，被 IoC 容器管理的对象都称为 **Bean**。

## 关键代码解读

### 1. 传统方式的问题

```java
User user = new User();
user.setId(1L);
user.setName("张三");
```

问题：
- 代码重复
- 对象创建逻辑分散在各处
- 难以修改和测试

### 2. Spring 方式

```java
// 通过容器获取 Bean，而不是自己 new
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
User user = (User) context.getBean("user");
```

好处：
- 不需要自己 new 对象
- 容器统一管理对象创建
- 易于修改和测试

## 扩展学习

学完这个示例后，建议继续学习：

1. **Spring Boot**：更快速的 Spring 开发体验
2. **依赖注入的更多方式**：构造函数注入、Setter 注入
3. **Bean 的作用域**：singleton、prototype 等
4. **Spring MVC**：构建 Web 应用
5. **Spring Data**：数据库访问

## 常见问题

### Q: Maven 下载依赖很慢怎么办？

A: 可以配置 Maven 使用阿里云镜像。在 `~/.m2/settings.xml` 中添加：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/central</url>
  </mirror>
</mirrors>
```

### Q: 编译失败怎么办？

A: 确保：
1. JDK 已正确安装（不是 JRE）
2. JAVA_HOME 环境变量已设置
3. Maven 可以访问网络下载依赖

## 参考资料

- [Spring Framework 官方文档](https://spring.io/projects/spring-framework)
- [Maven 官方文档](https://maven.apache.org/guides/)
