# Mall 微服务项目

简化版电商/订单系统，从单体应用逐步演进为完整的微服务架构。

## 项目结构

```
/workspace/2/
├── pom.xml                      # 父POM
├── docker-compose.yml           # 基础设施编排
├── init-sql/
│   └── 01-init.sql             # 数据库初始化脚本
├── mall-common/                 # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/mall/common/
│       ├── entity/             # 实体类
│       ├── constants/           # 常量
│       ├── response/            # 响应封装
│       └── utils/               # 工具类
├── mall-user/                   # 用户服务 (8081)
├── mall-product/                # 商品服务 (8082)
├── mall-order/                  # 订单服务 (8083)
├── mall-payment/                # 支付服务 (8084)
├── mall-admin/                  # 后台管理 (8085)
└── doc/                         # 文档
```

## 第 0 步：单体应用（当前）

启动基础设施：
```bash
docker-compose up -d
```

启动所有服务：
```bash
# 编译
mvn clean package -DskipTests

# 启动各个服务
java -jar mall-user/target/mall-user-1.0.0.jar &
java -jar mall-product/target/mall-product-1.0.0.jar &
java -jar mall-order/target/mall-order-1.0.0.jar &
java -jar mall-payment/target/mall-payment-1.0.0.jar &
java -jar mall-admin/target/mall-admin-1.0.0.jar &
```

### API 列表

**用户服务 (mall-user)**
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/{id} | 获取用户信息 |
| GET | /api/user/username/{username} | 根据用户名获取用户 |
| GET | /api/user/list | 分页获取用户列表 |

**商品服务 (mall-product)**
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/product | 创建商品 |
| PUT | /api/product | 更新商品 |
| GET | /api/product/{id} | 获取商品信息 |
| GET | /api/product/list | 分页获取商品列表 |
| GET | /api/product/search | 搜索商品 |
| POST | /api/product/deduct-stock | 扣减库存 |
| POST | /api/product/restore-stock | 恢复库存 |

**订单服务 (mall-order)**
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/order | 创建订单 |
| PUT | /api/order/cancel/{id} | 取消订单 |
| PUT | /api/order/status/{id} | 更新订单状态 |
| GET | /api/order/{id} | 获取订单信息 |
| GET | /api/order/list | 分页获取订单列表 |
| GET | /api/order/user/{userId} | 获取用户订单 |
| GET | /api/order/status/{status} | 按状态获取订单 |

**支付服务 (mall-payment)**
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/payment | 创建支付记录 |
| POST | /api/payment/simulate-pay/{id} | 模拟支付 |
| POST | /api/payment/refund/{id} | 退款 |
| GET | /api/payment/{id} | 获取支付信息 |
| GET | /api/payment/order/{orderNo} | 根据订单号获取支付 |

**后台管理 (mall-admin)**
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/admin/operation-log | 记录操作日志 |
| GET | /api/admin/health | 健康检查 |

## 演进步骤

### 第 1 步：Nacos
- 引入 Nacos 实现服务注册与发现
- 引入 Nacos 配置中心，统一管理配置
- 文件：nacos-compose.yml

### 第 2 步：Dubbo
- 引入 Dubbo RPC 框架
- order-service 远程调用 user-service、product-service
- 改用 Dubbo REST 或 Dubbo RPC 协议

### 第 3 步：Sentinel
- 引入 Sentinel 进行限流、熔断、降级
- 为 HTTP 接口和 Dubbo 调用添加保护

### 第 4 步：RocketMQ
- 引入 RocketMQ
- 订单创建后异步发送消息
- 扣库存、发通知、写日志等场景

### 第 5 步：Seata
- 引入 Seata AT 模式
- 订单服务 + 库存服务 + 账户服务分布式事务

### 第 6 步：XXL-JOB
- 引入 XXL-JOB 调度中心
- 定时取消超时订单
- 定时生成统计报表

### 第 7 步：Redisson
- 引入 Redisson
- 分布式锁、防重复提交
- 限流器、延迟队列

### 第 8 步：ShardingSphere
- 引入 ShardingSphere-JDBC
- 订单表按 user_id 或 order_id 分库分表

### 第 9 步：SkyWalking
- 引入 SkyWalking Agent
- 接入链路追踪
- 观察一次下单的完整链路

### 第 10 步：Arthas
- 使用 Arthas 进行线上诊断
- 排查慢接口
- 线程阻塞分析
- 方法耗时分析

## 技术栈版本

| 组件 | 版本 |
|------|------|
| Java | 1.8 |
| Spring Boot | 2.7.18 |
| Spring Cloud | 2021.0.8 |
| MyBatis Plus | 3.5.3.1 |
| Druid | 1.2.18 |
| MySQL | 8.0 |
| Redis | 7.0 |

## 数据库

- mall_user: 用户数据
- mall_product: 商品和库存
- mall_order: 订单数据
- mall_payment: 支付记录
- mall_admin: 操作日志

## 示例请求

### 创建用户
```bash
curl -X POST http://localhost:8081/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","realName":"Test User","phone":"13800138000","email":"test@mall.com"}'
```

### 创建商品
```bash
curl -X POST http://localhost:8082/api/product \
  -H "Content-Type: application/json" \
  -d '{"productName":"Test Product","price":99.99,"stock":100,"unit":"piece","category":"Test"}'
```

### 创建订单
```bash
curl -X POST http://localhost:8083/api/order \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productId":1,"quantity":1,"totalAmount":99.99,"shippingAddress":"Beijing","receiverName":"Test","receiverPhone":"13800138000"}'
```
