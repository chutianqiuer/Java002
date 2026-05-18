# Spring AOP 深入学习示例

本项目是Spring AOP（面向切面编程）的深入学习示例，通过详细的代码演示AOP的核心概念和实际应用。

## 项目结构

```
demo005-SpringAOP/
├── pom.xml                                          # Maven项目配置
├── src/main/java/com/example/
│   ├── MainApp.java                                 # 主程序入口
│   ├── aop/
│   │   ├── service/
│   │   │   ├── UserService.java                     # 用户服务类
│   │   │   └── OrderService.java                    # 订单服务类
│   │   ├── aspect/
│   │   │   ├── LoggingAspect.java                   # 日志切面（@Before/@After/@AfterReturning）
│   │   │   ├── PerformanceAspect.java                # 性能监控切面（@Around）
│   │   │   └── ValidationAspect.java                 # 验证切面（@AfterThrowing）
│   │   └── pointcut/
│   │       └── MyPointcut.java                      # 自定义切入点表达式
│   └── model/
│       └── User.java                                # 用户模型类
├── src/main/resources/
│   └── applicationContext.xml                       # Spring XML配置（AOP XML方式）
└── README.md                                        # 本文件
```

## AOP核心概念

本项目演示了AOP的7个核心概念：

| 概念 | 说明 | 对应代码 |
|------|------|----------|
| **Join Point（连接点）** | 程序执行中可以被拦截的点，通常指方法调用 | UserService、OrderService中的每个方法 |
| **Pointcut（切入点）** | 用于匹配连接点的表达式 | `execution(* com.example..*.*(..))` |
| **Advice（通知）** | 切入点执行的额外代码 | @Before、@After、@Around等注解 |
| **Aspect（切面）** | 切入点+通知的组合 | LoggingAspect、PerformanceAspect等 |
| **Weaving（织入）** | 将切面应用到目标对象的过程 | Spring AOP自动完成 |
| **Proxy（代理）** | 为目标对象创建的代理对象 | Spring自动创建的代理类 |
| **Target（目标对象）** | 被AOP代理的原始对象 | UserService、OrderService |

## 五种通知类型

| 通知类型 | 注解 | 执行时机 | 典型应用 |
|----------|------|----------|----------|
| 前置通知 | @Before | 目标方法执行前 | 参数验证、日志记录 |
| 后置通知 | @After | 目标方法执行后（无论是否异常） | 资源释放、清理工作 |
| 返回通知 | @AfterReturning | 目标方法正常返回时 | 结果日志、后处理 |
| 异常通知 | @AfterThrowing | 目标方法抛出异常时 | 异常日志、补偿处理 |
| 环绕通知 | @Around | 完全控制目标方法执行 | 性能监控、缓存、权限控制 |

## 切入点表达式

| 表达式 | 说明 | 示例 |
|--------|------|------|
| `execution()` | 匹配方法执行的连接点 | `execution(* com.example..*.*(..))` |
| `within()` | 匹配指定类型内的所有方法 | `within(com.example.service.*)` |
| `this()` | 匹配代理对象是指定类型的连接点 | `this(UserService)` |
| `target()` | 匹配目标对象是指定类型的连接点 | `target(UserService)` |
| `args()` | 匹配参数类型匹配的方法 | `args(Long, String)` |

## 依赖配置

项目使用Maven构建，主要依赖：

```xml
<dependencies>
    <!-- Spring核心容器 -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.3.20</version>
    </dependency>

    <!-- Spring AOP框架 -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-aop</artifactId>
        <version>5.3.20</version>
    </dependency>

    <!-- AspectJ运行时 -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjrt</artifactId>
        <version>1.9.9.1</version>
    </dependency>

    <!-- AspectJ编织器 -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.9.9.1</version>
    </dependency>
</dependencies>
```

## 运行方式

### 方式一：使用Maven运行

```bash
cd demo005-SpringAOP
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.MainApp"
```

### 方式二：使用IDEA/Eclipse

1. 使用IDE打开项目
2. 等待Maven下载依赖
3. 运行 `MainApp.java` 的main方法

### 方式三：打包后运行

```bash
mvn clean package
java -cp target/demo005-SpringAOP-1.0-SNAPSHOT.jar com.example.MainApp
```

## 运行结果示例

运行程序后，您将看到类似以下输出：

```
=================================================
       Spring AOP 演示程序启动
=================================================

【步骤1】创建Spring ApplicationContext容器...
【步骤1】完成：Spring容器创建成功！

【步骤2】从容器获取AOP代理对象...
【步骤2】完成：获取到的UserService代理对象类：com.sun.proxy.$Proxy18
        （可以看到是$Proxy开头，这是Spring AOP生成的代理类）

========== 【环绕通知-前置】 ==========
类名：UserService
方法名：public com.example.model.User com.example.aop.service.UserService.findById(java.lang.Long)
开始时间：1234567890ms
======================================
[PerformanceAspect] 调用目标方法...
[UserService] 执行findById方法，参数：id=1
========== 【前置通知】 ==========
方法名：public com.example.model.User com.example.aop.service.UserService.findById(java.lang.Long)
参数列表：[1]
================================
========== 【返回通知】 ==========
方法名：UserService.findById(..)
返回值：User{id=1, username='testUser', email='test@example.com'}
（仅在方法正常返回时执行）
================================
========== 【后置通知】 ==========
方法执行完成：UserService.findById(..)
无论是否异常，都会执行这里的代码！
================================
========== 【环绕通知-返回】 ==========
方法执行成功！
返回类型：User
返回结果：User{id=1, username='testUser', email='test@example.com'}
执行耗时：5ms
================================
========== 【环绕通知-最终】 ==========
finally块：执行清理工作
总执行时间：5ms
================================
```

## 实际应用场景

本项目演示了AOP在以下场景中的应用：

### 1. 日志记录（LoggingAspect）
- 记录方法调用前后参数和返回值
- 记录方法执行时间
- 记录异常信息

### 2. 性能监控（PerformanceAspect）
- 监控方法执行时间
- 对慢查询进行警告
- 使用纳秒级精度统计

### 3. 异常处理（ValidationAspect）
- 统一捕获和处理业务异常
- 针对不同异常类型进行不同处理
- 发送告警通知

### 4. 方法拦截（PerformanceAspect.batchOperation）
- 通过@Around通知阻止方法执行
- 实现权限控制、负载控制等功能

## 通知执行顺序

### 正常流程
```
@Before → 目标方法 → @AfterReturning → @After
```

### 异常流程
```
@Before → 目标方法（抛异常）→ @AfterThrowing → @After
```

### @Around通知（最完整）
```
@Around前置 → proceed() → @Around返回/异常 → @Around最终（finally）
```

## 切入点表达式语法

```
execution(
    修饰符? 返回类型
    包名.类名.方法名(参数类型)
    异常类型?
)
```

示例：
- `execution(* com.example..*.*(..))` - 匹配com.example包及子包下所有类的所有方法
- `execution(* *..findById(Long))` - 匹配任意包下名为findById、接收Long参数的方法
- `execution(* com.example.service.UserService+.*(..))` - 匹配UserService及其子类

## 注意事项

1. **Spring AOP vs AspectJ**：本项目使用Spring AOP，它是基于代理的AOP，只支持方法级别的拦截。如需更强大的AOP能力（如构造器拦截、属性拦截），请使用AspectJ。

2. **代理方式**：Spring AOP默认使用JDK动态代理（目标类实现了接口时）或CGLIB代理（目标类未实现接口或配置强制使用）。

3. **自调用问题**：Spring AOP无法拦截同类内部方法调用（self-invocation），因为这不经过代理对象。如需解决，可使用 AspectJ 或通过ApplicationContext获取代理对象。

## 参考资料

- [Spring AOP官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop)
- [AspectJ切入点语言](https://www.eclipse.org/aspectj/docs.php)
