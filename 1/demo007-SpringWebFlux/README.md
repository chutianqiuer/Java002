# Spring WebFlux 响应式编程示例

## 项目简介

本项目是Spring WebFlux响应式编程的深入学习示例，通过详细的代码和中文注释演示：

1. **为什么需要响应式编程** - 传统Servlet线程模型的局限性
2. **Spring WebFlux的两种编程模型** - 注解式和函数式
3. **Mono和Flux核心概念** - 0或1个元素 vs 0到N个元素
4. **响应式操作符** - map、filter、flatMap、zip等
5. **WebClient** - 非阻塞式HTTP客户端
6. **背压（Backpressure）** - 数据流控制机制

## 目录结构

```
demo007-SpringWebFlux/
├── pom.xml                                    # Maven配置文件
├── src/main/java/com/example/
│   ├── Demo007Application.java                # Spring Boot启动类
│   ├── controller/
│   │   ├── UserController.java               # 传统@Controller方式
│   │   └── ArticleController.java            # 文章控制器
│   ├── handler/
│   │   └── UserHandler.java                  # Handler函数（函数式）
│   ├── router/
│   │   └── UserRouter.java                   # 路由配置（函数式）
│   ├── model/
│   │   └── User.java                         # 用户实体类
│   ├── service/
│   │   └── UserService.java                  # 响应式服务层
│   └── repository/
│       └── UserRepository.java               # 响应式Repository
└── README.md                                 # 本文档
```

## 核心概念

### 1. 为什么需要响应式编程？

#### 传统Servlet线程模型的局限性

```
传统模型（同步阻塞）:
┌─────────────────────────────────────────────────────────────┐
│  Thread Pool (10 threads)                                  │
│  ┌────────┐ ┌────────┐ ┌────────┐      ┌────────┐         │
│  │Thread-1│ │Thread-2│ │Thread-3│ ...  │Thread-10│         │
│  └────┬───┘ └────┬───┘ └────┬───┘      └────┬───┘         │
│       │          │          │               │              │
│       ▼          ▼          ▼               ▼              │
│   Request-1  Request-2  Request-3       Request-10          │
│   (等待DB)   (等待DB)   (等待API)       (等待DB)            │
│       │          │          │               │              │
│   线程阻塞    线程阻塞    线程阻塞        线程阻塞            │
└─────────────────────────────────────────────────────────────┘

问题：
- 10000个并发请求需要10000个线程
- 每个线程约1MB内存 = 10GB内存
- IO等待时线程空闲，资源浪费
```

#### 响应式模型的优势

```
响应式模型（异步非阻塞）:
┌─────────────────────────────────────────────────────────────┐
│  Event Loop (1-2 threads)                                    │
│  ┌──────────────────────────────────────┐                   │
│  │         Single Event Loop            │                   │
│  │  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐  │                   │
│  │  │Req1│ │Req2│ │Req3│ │Req4│ │Req5│ ...                 │
│  │  └──┬─┘ └──┬─┘ └──┬─┘ └──┬─┘ └──┬─┘                     │
│  └─────┼──────┼──────┼──────┼──────┼───────────────────────┘
│        │      │      │      │      │
│        ▼      ▼      ▼      ▼      ▼
│   ┌─────────────────────────────────┐
│   │     Non-Blocking Operations     │
│   │  ┌─────────┐  ┌─────────┐       │
│   │  │   DB    │  │   API   │  ... │  ← 异步回调，不阻塞线程
│   │  └─────────┘  └─────────┘       │
│   └─────────────────────────────────┘
│        │      │      │      │      │
│        ▼      ▼      ▼      ▼      ▼
│      Response1 Response2 Response3 ...
└─────────────────────────────────────────────────────────────┘

优势：
- 只需少量线程（CPU核心数）处理大量并发
- 线程不等待IO，资源利用率高
- 更好处理突发流量
```

### 2. Mono vs Flux

```
Mono<T>  - 0或1个元素的异步序列
┌────────────────────────────────────────────────────┐
│  Mono.just(user)                                   │
│  ┌──────────┐                                      │
│  │   User   │ ──────────────────▶ 完成             │
│  └──────────┘                                      │
│                                                    │
│  Mono.empty()                                      │
│  ─────────────────────────────────────────▶ 完成   │
│                                                    │
│  适用场景: 单个资源、创建操作、计数操作              │
└────────────────────────────────────────────────────┘

Flux<T>  - 0到N个元素的异步序列
┌────────────────────────────────────────────────────┐
│  Flux.fromIterable(users)                          │
│  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐              │
│  │User│ │User│ │User│ │User│ │User│ ───▶ 完成    │
│  └────┘ └────┘ └────┘ └────┘ └────┘              │
│                                                    │
│  适用场景: 列表查询、流式数据、实时事件              │
└────────────────────────────────────────────────────┘
```

### 3. 两种编程模型对比

#### 注解式（@Controller）

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
```

**特点:**
- 与Spring MVC开发体验一致
- 易于从Spring MVC迁移
- 代码简洁直观

#### 函数式（RouterFunction + HandlerFunction）

```java
@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return route()
                .GET("/functional/users/{id}",
                     accept(APPLICATION_JSON),
                     handler::getUserById)
                .build();
    }
}

@Component
public class UserHandler {
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return userService.getUserById(id)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
```

**特点:**
- 路由逻辑集中管理
- 更灵活的路由配置
- 易于动态路由

### 4. 常用操作符

```java
// map - 同步转换
flux.map(user -> user.getName())  // User -> String

// filter - 过滤
flux.filter(user -> user.getAge() > 20)  // 过滤年龄大于20

// flatMap - 异步转换
flux.flatMap(id -> fetchUserById(id))  // 返回Mono/Flux

// zip - 组合多个流
Mono.zip(mono1, mono2, (a, b) -> a + b)

// switchIfEmpty - 空时切换
mono.switchIfEmpty(Mono.just(defaultValue))

// onErrorResume - 错误时恢复
mono.onErrorResume(e -> Mono.just(fallbackValue))

// take - 取前N个
flux.take(10)

// collectList - 收集到列表
flux.collectList()  // Flux<T> -> Mono<List<T>>

// count - 计数
flux.count()  // Flux<T> -> Mono<Long>
```

### 5. 背压（Backpressure）

```
无背压（危险）:
Producer ────10000条/秒───▶ Consumer
              (内存溢出!)

有背压（安全）:
Producer ◀───── request(100) ──── Consumer
   │                                     │
   │  发送100条后等待                    │  处理完再请求
   │◀────────────────────────────────────│
   │                                     │
   ▼                                     ▼
 100条       100条         100条       100条

背压实现方式:
- take(100) - 只取前100个
- buffer(100) - 缓冲100个再处理
- limitRate(100) - 限制速率
```

## 运行项目

### 环境要求

- JDK 17+
- Maven 3.6+

### 编译和运行

```bash
# 编译项目
cd demo007-SpringWebFlux
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/demo007-SpringWebFlux-1.0.0.jar
```

### 访问端点

#### 注解式控制器（UserController）

| 方法   | 路径                    | 说明             |
|--------|-------------------------|------------------|
| GET    | /api/users              | 获取所有用户     |
| GET    | /api/users/{id}         | 获取单个用户     |
| POST   | /api/users              | 创建用户         |
| PUT    | /api/users/{id}         | 更新用户         |
| DELETE | /api/users/{id}         | 删除用户         |
| GET    | /api/users/search       | 搜索用户         |
| GET    | /api/users/count        | 获取用户数量     |
| GET    | /api/users/safe/{id}    | 安全获取用户     |
| GET    | /api/users/demo/operators | 演示操作符     |

#### 函数式端点（UserRouter + UserHandler）

| 方法   | 路径                          | 说明             |
|--------|-------------------------------|------------------|
| GET    | /functional/users             | 获取所有用户     |
| GET    | /functional/users/{id}       | 获取单个用户     |
| POST   | /functional/users             | 创建用户         |
| PUT    | /functional/users/{id}       | 更新用户         |
| DELETE | /functional/users/{id}       | 删除用户         |
| GET    | /functional/users/search     | 搜索用户         |
| POST   | /functional/users/process    | 批量处理用户     |

#### 文章相关端点

| 方法   | 路径                     | 说明             |
|--------|--------------------------|------------------|
| GET    | /api/articles            | 获取所有文章     |
| GET    | /api/articles/{id}       | 获取单篇文章     |
| GET    | /api/articles/stats     | 获取统计信息     |
| GET    | /api/articles/search    | 搜索文章         |

### 测试示例

```bash
# 获取所有用户（注解式）
curl http://localhost:8080/api/users

# 获取所有用户（函数式）
curl http://localhost:8080/functional/users

# 获取单个用户
curl http://localhost:8080/api/users/1

# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"测试用户","email":"test@example.com","age":25}'

# 更新用户
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"更新后的名字","age":30}'

# 删除用户
curl -X DELETE http://localhost:8080/api/users/5

# 搜索用户
curl "http://localhost:8080/api/users/search?name=张&limit=10"

# 演示操作符
curl http://localhost:8080/api/users/demo/operators

# 安全获取（错误处理）
curl http://localhost:8080/api/users/safe/999

# 获取文章
curl http://localhost:8080/api/articles

# 获取文章统计
curl http://localhost:8080/api/articles/stats
```

## 响应式编程最佳实践

### 1. 避免阻塞操作

```java
// ❌ 错误：在响应式链中使用block()
Flux<User> users = userRepository.findAll()
    .map(user -> {
        // 这里调用阻塞方法！
        return blockingService.getData(user.getId());  // 不要这样做
    });

// ✅ 正确：使用flatMap进行异步组合
Flux<User> users = userRepository.findAll()
    .flatMap(user ->
        reactiveService.getData(user.getId())  // 返回Mono/Flux
    );
```

### 2. 正确的错误处理

```java
// 使用onErrorResume提供备用方案
userService.getUserById(id)
    .onErrorResume(e -> Mono.just(defaultUser))
    .subscribe();

// 使用switchIfEmpty处理空结果
userService.findByEmail(email)
    .switchIfEmpty(Mono.error(new UserNotFoundException()))
    .subscribe();
```

### 3. 合理使用subscribe

```java
// 在Web请求处理中不需要手动subscribe
// Spring会自动处理

// ❌ 错误
@GetMapping("/users")
public Flux<User> getUsers() {
    return userRepository.findAll()
        .subscribe();  // 不要这样做！
}

// ✅ 正确：直接返回Flux/Mono
@GetMapping("/users")
public Flux<User> getUsers() {
    return userRepository.findAll();  // Spring会处理subscribe
}
```

### 4. 背压控制

```java
// take限制数量
userRepository.findAll()
    .take(100)  // 最多返回100个

// limitRate限制速率
userRepository.findAll()
    .limitRate(100)  // 每次最多处理100个

// 使用buffer缓冲
userRepository.findAll()
    .buffer(100)  // 收集100个后再处理
```

## 学习资源

- [Spring WebFlux官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Project Reactor官方文档](https://projectreactor.io/docs/core/release/reference/)
- [Reactor 3 Reference Guide](https://projectreactor.io/docs/core/release/reference/)
- [WebClient官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client)

## 总结

Spring WebFlux为现代高并发应用提供了强大的响应式编程能力：

1. **更高的并发能力** - 少量线程处理大量请求
2. **更好的资源利用** - 非阻塞IO，不浪费等待时间
3. **优雅的异步处理** - 使用Mono/Flux表示异步操作
4. **灵活的数据流控制** - 背压机制防止系统过载
5. **两种编程模型** - 注解式易于上手，函数式更灵活

通过本项目的学习和实践，你将掌握响应式Web开发的核心概念和技能。
