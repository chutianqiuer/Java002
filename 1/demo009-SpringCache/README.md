# Spring Cache 缓存抽象演示项目

## 项目简介

本项目是Spring Cache（缓存抽象）的深入学习示例，演示了Spring Framework提供的统一缓存API的使用方法和核心概念。

### 核心特点

**Spring Cache的设计理念：缓存与业务代码解耦**

```
┌─────────────────────────────────────────────────────────────────┐
│                        业务代码层                                │
│   ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│   │  @Cacheable     │  │  @CacheEvict    │  │  @CachePut      │ │
│   │  缓存查询结果   │  │  清除缓存       │  │  更新缓存       │ │
│   └────────┬────────┘  └────────┬────────┘  └────────┬────────┘ │
└────────────┼───────────────────┼───────────────────┼──────────┘
             │                   │                   │
             ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Cache 抽象层                         │
│                   （统一的缓存操作接口）                          │
└─────────────────────────────────────────────────────────────────┘
             │                   │                   │
             ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                      缓存实现层（可切换）                         │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────┐ │
│   │ Caffeine   │  │  EhCache    │  │    Redis   │  │ 其他... │ │
│   │ (本地缓存)  │  │ (进程内缓存) │  │ (分布式缓存) │  │         │ │
│   └─────────────┘  └─────────────┘  └─────────────┘  └─────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 技术栈

- **Spring Boot**: 2.7.18
- **Spring Framework**: 5.3.x（包含spring-context、spring-context-support）
- **EhCache**: 3.10.8（JCache标准实现）
- **Caffeine**: 高性能本地缓存
- **Maven**: 3.x
- **Java**: 1.8+

## 项目结构

```
demo009-SpringCache/
├── pom.xml                                    # Maven配置文件
├── src/main/java/com/example/
│   ├── Demo009Application.java               # 启动类
│   ├── config/
│   │   └── CacheConfig.java                  # 缓存配置类
│   ├── service/
│   │   ├── UserService.java                  # 用户服务（核心注解演示）
│   │   └── ProductService.java               # 产品服务（多级缓存/注意事项）
│   ├── model/
│   │   └── User.java                         # 用户实体类
│   └── cache/
│       └── MyCacheManager.java               # 自定义CacheManager
├── src/main/resources/
│   ├── ehcache.xml                           # EhCache配置文件
│   └── application.properties                # Spring Boot配置
└── README.md                                 # 本文件
```

## 核心注解详解

### 1. @Cacheable - 缓存查询

```java
/**
 * 【执行流程】
 * 1. 检查缓存是否存在key
 * 2. 存在 → 直接返回缓存值（不执行方法）
 * 3. 不存在 → 执行方法，将结果存入缓存
 */
@Cacheable(value = "user", key = "#id")
public User getUserById(Long id) {
    // 只有缓存未命中时才会执行
    return userRepository.findById(id);
}
```

### 2. @CachePut - 更新缓存

```java
/**
 * 【执行流程】
 * 无论缓存是否存在，都执行方法，并更新缓存
 */
@CachePut(value = "user", key = "#user.id")
public User updateUser(User user) {
    return userRepository.save(user);
}
```

### 3. @CacheEvict - 清除缓存

```java
/**
 * 【执行流程】
 * 方法执行后清除指定缓存
 */
@CacheEvict(value = "user", key = "#id")
public void deleteUser(Long id) {
    userRepository.deleteById(id);
}

// 清除所有缓存
@CacheEvict(value = "user", allEntries = true)
public void clearAllUsers() {
    userRepository.deleteAll();
}
```

### 4. @Caching - 组合注解

```java
@Caching(
    put = {
        @CachePut(value = "userDetail", key = "#user.id"),
        @CachePut(value = "userIndex", key = "#user.name")
    },
    evict = {
        @CacheEvict(value = "userList", allEntries = true)
    }
)
public User updateUserAndEvictList(User user) {
    return userRepository.save(user);
}
```

### 5. @CacheConfig - 类级别配置

```java
@Service
@CacheConfig(cacheNames = "users", keyGenerator = "myKeyGenerator")
public class UserService {
    // 所有方法默认使用users缓存
    // 不需要在每个方法上指定cacheNames
}
```

## SpEL表达式

| 表达式 | 说明 |
|--------|------|
| `#id` | 引用方法参数id |
| `#user.id` | 引用user参数的id属性 |
| `#user.name` | 引用user参数的name属性 |
| `#result` | 引用方法返回值 |
| `#root.methodName` | 获取当前方法名 |
| `#root.targetClass` | 获取目标类 |

## 缓存条件控制

```java
// condition：方法执行前判断
@Cacheable(value = "user", key = "#id", condition = "#id > 0")
public User getUserById(Long id) { ... }

// unless：方法执行后判断，条件为true时不缓存
@Cacheable(value = "user", key = "#id", unless = "#result == null")
public User getUserById(Long id) { ... }
```

## 运行项目

### 1. 编译项目

```bash
cd demo009-SpringCache
mvn clean compile
```

### 2. 运行测试

```bash
mvn test
```

### 3. 运行应用程序

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn package -DskipTests
java -jar target/demo009-SpringCache-1.0.0.jar
```

## 演示内容

### UserService - 核心注解演示

| 方法 | 演示内容 |
|------|---------|
| `getUserById()` | @Cacheable基本用法 |
| `getUserByIdWithCondition()` | condition条件控制 |
| `getUserByIdWithUnless()` | unless条件控制 |
| `getUserByIdWithGenerator()` | 自定义KeyGenerator |
| `updateUser()` | @CachePut更新缓存 |
| `deleteUser()` | @CacheEvict清除缓存 |
| `clearAllUsers()` | allEntries=true清除所有 |
| `updateUserAndEvictList()` | @Caching组合注解 |

### ProductService - 多级缓存与注意事项

| 方法 | 演示内容 |
|------|---------|
| `getProductInfo()` | 多级缓存查询流程 |
| `getProductInfoWithAnnotation()` | @Cacheable注解效果 |
| `getProductInfoWithPenetration()` | 缓存穿透应对策略 |
| `getProductInfoWithAvalanche()` | 缓存雪崩应对策略 |
| `getProductInfoWithBreakdown()` | 缓存击穿应对策略 |
| `getProductInfoWithLogicalExpire()` | 逻辑过期方案 |

## 缓存三大注意事项

### 1. 缓存穿透

```
问题：大量请求查询不存在的数据，直接打到数据库

原因：
- 数据不存在，缓存中也没有
- 恶意大量请求不存在的数据

解决方案：
a) 缓存空值：给空结果也设置短TTL缓存
b) 布隆过滤器：快速判断数据是否存在
c) 接口校验：参数合法性校验
```

### 2. 缓存雪崩

```
问题：大量缓存同时过期，导致大量请求同时穿透到数据库

原因：
- 缓存TTL设置相同，大量缓存同时过期
- Redis宕机

解决方案：
a) 随机TTL：为缓存TTL添加随机值
b) 多级缓存：L1 + L2 + L3
c) 熔断降级：使用Sentinel/Hystrix
d) 预热缓存：启动时加载热点数据
```

### 3. 缓存击穿

```
问题：某个热点数据过期瞬间，大量请求同时查询该数据

原因：
- 热点数据突然过期
- 缓存重建需要时间

解决方案：
a) 互斥锁：只有一个线程重建缓存
b) 永不过期：对热点数据设置永不过期
c) 逻辑过期：数据永不过期，靠异步更新
```

## 切换缓存实现

### ConcurrentHashMap（开发环境）

```java
// 简单内存缓存，不支持过期和淘汰
@Bean
public CacheManager simpleCacheManager() {
    return new ConcurrentMapCacheManager("users", "products");
}
```

### Caffeine（生产环境推荐）

```java
// 高性能本地缓存，支持多种过期策略
@Bean
public CacheManager caffeineCacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .recordStats());
    return cacheManager;
}
```

### EhCache（分布式缓存）

```xml
<!-- ehcache.xml -->
<cache alias="users">
    <heap unit="entries">1000</heap>
    <expiry><ttl unit="m">30</ttl></expiry>
</cache>
```

### Redis（需要添加spring-boot-starter-data-redis依赖）

```yaml
# application.yml
spring:
  redis:
    host: localhost
    port: 6379
```

```java
@Bean
public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)))
        .build();
}
```

## 依赖配置对照表

| 缓存实现 | Maven依赖 | 配置方式 |
|---------|----------|--------|
| ConcurrentHashMap | spring-context（内置） | ConcurrentMapCacheManager |
| EhCache 2.x | ehcache-core | EhCacheManagerFactoryBean |
| EhCache 3.x | ehcache + cache-api | JCacheCacheManager |
| Caffeine | caffeine | CaffeineCacheManager |
| Guava Cache | guava | GuavaCacheManager |
| Redis | spring-boot-starter-data-redis | RedisCacheManager |

## 注意事项

1. **@EnableCaching**：必须在配置类或启动类上添加
2. **Serializable**：缓存对象建议实现Serializable接口
3. **线程安全**：Spring Cache本身是线程安全的
4. **缓存粒度**：避免缓存过大的数据
5. **过期策略**：根据业务特点选择合适的过期策略
6. **监控**：生产环境建议开启缓存统计功能

## 参考资料

- [Spring Cache官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
- [EhCache官方文档](https://www.ehcache.org/documentation/)
- [Caffeine GitHub](https://github.com/ben-manes/caffeine)
- [JCache标准](https://www.jcp.org/en/jsr/detail?id=107)
