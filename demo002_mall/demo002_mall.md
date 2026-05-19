# demo002_mall 启动示例

## 环境要求

- Java 21 (需添加 `--add-opens` JVM 参数)
- Docker & Docker Compose
- Maven 3.9+

## 目录结构

```
demo002_mall/
├── docker-compose.yml          # MySQL / Redis / Nacos 基础设施
├── pom.xml                     # 父 POM，Java 1.8 + Spring Boot 2.7.18
├── start-services.sh           # 服务启动脚本
├── init-sql/                   # 数据库初始化脚本
├── mall-common/                # 公共模块
├── mall-user/                  # 用户服务 (Dubbo Provider)
├── mall-product/               # 商品服务 (Dubbo Provider)
├── mall-order/                 # 订单服务 (Dubbo Consumer)
├── mall-payment/               # 支付服务
└── mall-admin/                 # 管理服务
```

## 快速启动

### 1. 启动基础设施
```bash
cd demo002_mall
docker compose up -d
```

验证容器状态：

```bash
docker ps --format "table {{.Names}}\t{{.Status}}"
```

等待输出：

```
NAMES        STATUS
mall-redis   Up (healthy)
mall-mysql   Up (healthy)
mall-nacos   Up
```

### 2. 构建项目

```bash
mvn clean package -DskipTests
```

### 3. 启动微服务

```bash
bash start-services.sh
```

启动顺序：mall-user -> mall-product -> mall-order -> mall-payment -> mall-admin

### 4. 验证服务

#### HTTP 接口

```bash
curl http://localhost:8081/api/user/list
curl http://localhost:8082/api/product/list
curl http://localhost:8083/api/order/list
curl http://localhost:8084/api/payment/1
curl http://localhost:8085/api/admin/health
```

预期：所有接口返回 `{"code":200,"message":"success",...}`

#### Nacos 服务注册

```bash
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=mall-user"
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=mall-product"
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=mall-order"
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=mall-payment"
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=mall-admin"
```

预期：每个服务返回 `"healthy":true`

## 服务端口

| 服务 | HTTP 端口 | Dubbo 端口 |
|------|----------|-----------|
| mall-user | 8081 | 20881 |
| mall-product | 8082 | 20882 |
| mall-order | 8083 | 20883 |
| mall-payment | 8084 | - |
| mall-admin | 8085 | - |

## Nacos 控制台

访问 http://localhost:8848/nacos/index.html

- 用户名: `nacos`
- 密码: `nacos`

## 常见问题

### Q: 服务启动失败，提示 `No provider available`

A: 这是因为 Dubbo Consumer（mall-order）在 Provider（mall-user/mall-product）尚未注册到 Nacos 时就发起调用。项目已通过 `@DubboReference(check = false)` 解决启动阻塞问题，运行时调用仍走 Dubbo RPC。

### Q: `qos-server can not bind localhost:22222`

A: 非致命警告。Dubbo QoS 端口 22222 被首个启动的服务占用，后续服务会跳过。不影响功能。

### Q: Java 21 兼容性

A: 项目 pom.xml 指定 Java 1.8，但环境为 Java 21 时需要添加 JVM 参数。启动脚本已配置：

```bash
--add-opens java.base/java.math=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.lang.reflect=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
```

### Q: 如何查看日志

```bash
tail -f /tmp/mall-logs/mall-user.log
tail -f /tmp/mall-logs/mall-product.log
tail -f /tmp/mall-logs/mall-order.log
tail -f /tmp/mall-logs/mall-payment.log
tail -f /tmp/mall-logs/mall-admin.log
```

### Q: 如何停止服务

```bash
pkill -f "mall-.*1.0.0.jar"
```

### Q: 如何停止基础设施

```bash
docker compose down
```
