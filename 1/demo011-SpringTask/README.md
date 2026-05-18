# Spring Task 任务调度与异步执行示例

## 项目简介

本项目是Spring Task（任务调度与异步执行）的深入学习示例，通过详细的代码注释帮助开发者理解Spring Framework提供的任务调度和异步执行机制。

## 技术栈

- **Spring Framework**: 6.1.3
- **Java**: 17+
- **构建工具**: Maven 3.x
- **日志框架**: SLF4J + JCL

## 项目结构

```
demo011-SpringTask/
├── pom.xml                              # Maven配置文件
├── src/main/java/com/example/
│   ├── MainApp.java                     # 启动类
│   ├── config/
│   │   └── TaskConfig.java              # 任务调度配置类
│   ├── task/
│   │   ├── SimpleScheduledTask.java     # 固定频率/延迟任务
│   │   ├── CronScheduledTask.java       # Cron表达式任务
│   │   └── DynamicScheduledTask.java    # 动态任务调度
│   ├── async/
│   │   ├── AsyncService.java            # 异步服务
│   │   └── AsyncConfig.java            # 异步配置
│   └── model/
│       └── TaskResult.java              # 任务结果模型
└── README.md                            # 项目说明文档
```

## 核心概念

### 1. 任务调度（Task Scheduling）

任务调度是根据时间来执行任务的一种机制，Spring通过`@Scheduled`注解支持定时任务。

**主要特性：**
- 支持多种调度方式：固定延迟、固定频率、Cron表达式
- 使用场景：数据备份、定时统计、缓存刷新、邮件发送等

### 2. 异步执行（Async Execution）

异步执行是不在主线程中执行任务，而是在线程池中异步执行，提高程序并发性。

**主要特性：**
- 使用`@Async`注解支持异步方法调用
- 使用`TaskExecutor`管理线程池
- 提高程序并发性，避免阻塞主线程

## 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 编译项目

```bash
cd demo011-SpringTask
mvn clean compile
```

### 运行项目

```bash
mvn exec:java
```

或使用以下命令编译并运行：

```bash
mvn clean compile exec:java
```

## 核心功能演示

### 1. @Scheduled 定时任务

#### 1.1 fixedDelay - 固定延迟

```java
@Scheduled(fixedDelay = 3000, initialDelay = 5000)
public void taskWithFixedDelay() {
    // 上次执行完毕后，等待3秒再执行
    // 容器启动后延迟5秒开始第一次执行
}
```

**特点：**
- 上次执行完成后开始计算延迟时间
- 适合执行时间不确定的任务
- 确保任务不堆积

#### 1.2 fixedRate - 固定频率

```java
@Scheduled(fixedRate = 2000)
public void taskWithFixedRate() {
    // 上次开始执行后，等待2秒再执行
}
```

**特点：**
- 上次开始执行后开始计算频率时间
- 如果任务执行时间 > rate值，会导致任务连续执行（追赶模式）
- 适合执行时间稳定的任务

#### 1.3 initialDelay - 初始延迟

```java
@Scheduled(fixedDelay = 5000, initialDelay = 10000)
public void taskWithInitialDelay() {
    // 容器启动后延迟10秒开始，每5秒执行一次
}
```

### 2. Cron表达式

Cron表达式是最灵活的时间调度配置方式。

#### 2.1 格式说明

```
[秒] [分] [时] [日] [月] [星期] [年(可选)]
```

#### 2.2 特殊字符

| 字符 | 含义 | 示例 |
|------|------|------|
| `*` | 每 | `*`在秒字段表示每秒 |
| `,` | 值列表 | `1,3,5`表示1、3、5 |
| `-` | 范围 | `1-5`表示1到5 |
| `/` | 步长 | `0/5`表示0秒开始每5秒 |
| `?` | 不指定 | 日和星期字段使用 |
| `L` | 最后 | `L`表示最后一天 |
| `W` | 工作日 | `15W`表示15号最近的工作日 |
| `#` | 第几个 | `6#3`表示第3个周五 |

#### 2.3 常用表达式

| 表达式 | 含义 |
|--------|------|
| `0 0 * * * ?` | 每小时整点执行 |
| `0 0/5 * * * ?` | 每5分钟执行一次 |
| `0 0 8 * * ?` | 每天早上8点执行 |
| `0 30 8 ? * MON-FRI` | 工作日早上8点30分执行 |
| `0 0 0 15 * ?` | 每月15号凌晨0点执行 |
| `0 0 0 LW * ?` | 每月最后工作日凌晨0点执行 |

### 3. @Async 异步注解

#### 3.1 基本用法

```java
@Async
public void asyncTask() {
    // 方法会在独立线程中异步执行
}
```

#### 3.2 带返回值的异步方法

**使用Future：**
```java
@Async
public Future<String> asyncTaskWithFuture() {
    // 返回AsyncResult
    return new AsyncResult<>("result");
}
```

**使用CompletableFuture：**
```java
@Async
public CompletableFuture<String> asyncTaskWithCompletableFuture() {
    // 更灵活的异步编程
    return CompletableFuture.completedFuture("result");
}
```

#### 3.3 重要限制：同类内部调用失效

```java
@Service
public class MyService {

    // 错误示例：同类内部调用会导致@Async失效
    public void wrongCall() {
        asyncTask();  // 同步执行！
    }

    @Async
    public void asyncTask() {
        // 不会异步执行
    }

    // 正确示例：通过注入自身调用
    @Autowired
    private MyService self;

    public void correctCall() {
        self.asyncTask();  // 异步执行
    }
}
```

### 4. TaskExecutor 任务执行器

ThreadPoolTaskExecutor配置示例：

```java
@Bean
public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);      // 核心线程数
    executor.setMaxPoolSize(20);        // 最大线程数
    executor.setQueueCapacity(100);     // 队列容量
    executor.setThreadNamePrefix("async-");  // 线程名称前缀
    executor.initialize();
    return executor;
}
```

## 常见错误和注意事项

### 1. @Scheduled失效

**原因：** 没有添加`@EnableScheduling`注解

**解决：** 在配置类或启动类上添加`@EnableScheduling`

### 2. @Async失效

**原因：** 同类内部调用，不走代理

**解决：** 通过注入自身（`@Autowired`）调用

### 3. 任务堆积

**原因：** `fixedRate`任务执行时间超过指定的rate值

**解决：** 使用`fixedDelay`或增加线程池大小

### 4. 线程池未初始化

**原因：** 没有调用`executor.initialize()`

**解决：** 创建ThreadPoolTaskExecutor后必须调用initialize()

### 5. 任务未完成应用就关闭

**原因：** 没有正确配置关闭策略

**解决：** 配置`setWaitForTasksToCompleteOnShutdown(true)`

## 最佳实践

1. **合理设置线程池大小**
   - CPU密集型：核心线程数 = CPU核心数 + 1
   - IO密集型：核心线程数 = CPU核心数 × 2 或更多

2. **使用有界队列**
   - 避免任务无限堆积导致内存溢出
   - 设置合理的队列容量

3. **配置任务拒绝策略**
   - 生产环境建议使用`CallerRunsPolicy`
   - 避免任务丢失

4. **正确处理异常**
   - 异步任务要有异常处理机制
   - 使用try-catch或`@Async`配合`CompletableFuture`

5. **避免同步调用**
   - `@Async`方法不应返回void然后同步等待
   - 使用`Future`或`CompletableFuture`获取结果

## 扩展阅读

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Java Concurrency Basics](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [Cron Expression Generator](https://www.freeformatter.com/cron-expression-generator-quartz.html)

## 许可证

本项目仅供学习参考，使用MIT许可证。
