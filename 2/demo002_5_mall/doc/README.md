# Mall 微服务项目

简化版电商/订单系统，从单体应用逐步演进为完整的微服务架构。

## 技术栈（当前）

| 技术 | 状态 | 说明 |
|------|------|------|
| Spring Boot 2.7.18 | ✅ 已使用 | 基础框架 |
| MySQL 8.0 | ✅ 已使用 | 5 个独立数据库 |
| Redis 7.0 | ✅ 已使用 | 缓存 |
| Nacos 2.2.3 | ✅ 已使用 | 服务注册与发现 |
| Dubbo 3.1.10 | ✅ 已使用 | RPC 服务间调用 |
| MyBatis Plus 3.5.3 | ✅ 已使用 | ORM |
| Spring Cloud 2021.0.8 | ✅ 已使用 | 基础设施 |
| Sentinel | ⏳ 待接入 | 限流、熔断、降级 |
| RocketMQ | ⏳ 待接入 | 异步消息、订单完成后事件 |
| Seata | ⏳ 待接入 | 分布式事务 |
| XXL-JOB | ⏳ 待接入 | 定时任务（订单超时取消） |

## 项目结构

```
demo002_mall/
├── docker-compose.yml           # MySQL / Redis / Nacos 基础设施
├── pom.xml                     # 父 POM
├── start-services.sh           # 服务启动脚本
├── init-sql/
│   └── 01-init.sql             # 数据库初始化（5 个库 + 样例数据）
├── mall-common/                # 公共模块（实体、RPC 接口、响应封装）
├── mall-user/                  # 用户服务（Dubbo Provider）
├── mall-product/               # 商品服务（Dubbo Provider）
├── mall-order/                 # 订单服务（Dubbo Consumer）
├── mall-payment/               # 支付服务
├── mall-admin/                 # 管理服务
└── doc/
    └── README.md               # 本文档
```

## 服务端口

| 服务 | HTTP 端口 | Dubbo 端口 | 说明 |
|------|----------|-----------|------|
| mall-user | 8081 | 20881 | Dubbo Provider，提供用户查询 |
| mall-product | 8082 | 20882 | Dubbo Provider，提供商品查询/库存 |
| mall-order | 8083 | 20883 | Dubbo Consumer，依赖 user/product |
| mall-payment | 8084 | - | 支付服务 |
| mall-admin | 8085 | - | 管理服务 |

## 快速启动

### 1. 启动基础设施

```bash
cd demo002_mall
docker compose up -d
```

等待容器健康（约 20 秒）：

```bash
docker ps --format "table {{.Names}}\t{{.Status}}"
```

### 2. 构建项目

```bash
mvn clean package -DskipTests
```

### 3. 启动微服务

```bash
bash start-services.sh
```

### 4. 验证

```bash
curl http://localhost:8081/api/user/list
curl http://localhost:8082/api/product/list
curl http://localhost:8083/api/order/list
curl http://localhost:8084/api/payment/1
curl http://localhost:8085/api/admin/health
```

预期：所有返回 `{"code":200,"message":"success",...}`

### 5. Nacos 控制台

访问 http://localhost:8848/nacos/index.html

- 用户名: `nacos`
- 密码: `nacos`

可查看服务注册实例、健康状态。

## API 列表

### 用户服务 (mall-user) - 端口 8081

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/{id} | 获取用户信息 |
| GET | /api/user/username/{username} | 根据用户名获取用户 |
| GET | /api/user/list | 分页获取用户列表 |

### 商品服务 (mall-product) - 端口 8082

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/product | 创建商品 |
| PUT | /api/product | 更新商品 |
| GET | /api/product/{id} | 获取商品信息 |
| GET | /api/product/list | 分页获取商品列表 |
| GET | /api/product/search | 搜索商品 |
| POST | /api/product/deduct-stock | 扣减库存 |
| POST | /api/product/restore-stock | 恢复库存 |

### 订单服务 (mall-order) - 端口 8083

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/order | 创建订单 |
| PUT | /api/order/cancel/{id} | 取消订单 |
| PUT | /api/order/status/{id} | 更新订单状态 |
| GET | /api/order/{id} | 获取订单信息 |
| GET | /api/order/list | 分页获取订单列表 |
| GET | /api/order/user/{userId} | 获取用户订单 |
| GET | /api/order/status/{status} | 按状态获取订单 |

### 支付服务 (mall-payment) - 端口 8084

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/payment | 创建支付记录 |
| POST | /api/payment/simulate-pay/{id} | 模拟支付回调 |
| POST | /api/payment/refund/{id} | 退款 |
| GET | /api/payment/{id} | 获取支付信息 |
| GET | /api/payment/order/{orderNo} | 根据订单号获取支付 |

### 后台管理 (mall-admin) - 端口 8085

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/admin/operation-log | 记录操作日志 |
| GET | /api/admin/health | 健康检查 |

## Dubbo RPC 关系

```
mall-order (Consumer)
  ├── @DubboReference UserRpcService  →  mall-user (Provider) :20881
  └── @DubboReference ProductRpcService → mall-product (Provider) :20882
```

订单创建时通过 Dubbo RPC 验证用户和商品，不走 HTTP。

## 数据库

| 数据库 | 对应服务 |
|--------|---------|
| mall_user | mall-user |
| mall_product | mall-product |
| mall_order | mall-order |
| mall_payment | mall-payment |
| mall_admin | mall-admin |

## 演进步骤（待办）

### 第 1 步：Sentinel（限流、熔断、降级）⏳

- [ ] 引入 Sentinel
- [ ] 为 HTTP 接口添加限流规则
- [ ] 为 Dubbo RPC 调用添加熔断策略
- [ ] 集成 Nacos 作为规则数据源

### 第 2 步：RocketMQ（异步消息）⏳

- [ ] 引入 RocketMQ
- [ ] 订单创建成功后发送消息到 MQ
- [ ] 库存服务消费消息扣减库存
- [ ] 支付服务消费消息更新订单状态

### 第 3 步：Seata（分布式事务）⏳

- [ ] 引入 Seata AT 模式
- [ ] 改造库存扣减为 Seata 事务
- [ ] 订单创建 + 库存扣减纳入同一事务

### 第 4 步：XXL-JOB（定时任务）⏳

- [ ] 引入 XXL-JOB 调度中心
- [ ] 定时扫描超时未支付订单并取消
- [ ] 定时清理过期操作日志

## 常见问题

### Q: mall-order 启动失败，提示 `No provider available`

A: 这是 Dubbo Consumer 启动时强依赖 Provider 的经典问题。已在代码中添加 `check = false`，启动时不阻塞，运行时调用走 Dubbo RPC。

### Q: `qos-server can not bind localhost:22222`

A: 非致命警告。Dubbo QoS 端口 22222 被首个启动的服务绑定，后续服务跳过。不影响功能。

### Q: Java 21 兼容性

A: pom.xml 指定 Java 1.8，但环境为 Java 21 时需要 `--add-opens` 参数。启动脚本已配置，勿删除。

### Q: 如何查看日志

```bash
tail -f /tmp/mall-logs/mall-user.log
tail -f /tmp/mall-logs/mall-product.log
tail -f /tmp/mall-logs/mall-order.log
tail -f /tmp/mall-logs/mall-payment.log
tail -f /tmp/mall-logs/mall-admin.log
```

### Q: 如何停止所有服务

```bash
pkill -f "mall-.*1.0.0.jar"
```

### Q: 如何停止基础设施

```bash
docker compose down
```
