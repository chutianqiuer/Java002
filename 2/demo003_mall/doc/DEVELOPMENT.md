# Demo003 Mall 开发文档

## 项目概述

Demo003 Mall 是基于 Nacos + Dubbo 的微服务电商项目，从 demo002_mall 复制而来。包含以下服务：

- mall-user: 用户服务
- mall-product: 商品服务
- mall-order: 订单服务
- mall-payment: 支付服务
- mall-admin: 管理服务

## 技术栈

- Spring Boot 2.7.18
- Dubbo 3.x (RPC框架)
- Nacos (服务注册与发现)
- MyBatis Plus (ORM)
- MySQL 8.x (数据库)

## 项目结构

```
demo003_mall/
├── mall-common/          # 公共模块，包含实体类、RPC接口、响应类
├── mall-user/            # 用户服务
├── mall-product/          # 商品服务
├── mall-order/            # 订单服务
├── mall-payment/          # 支付服务
├── mall-admin/           # 管理服务
├── scripts/              # 测试脚本
├── start-services.sh     # 服务启动脚本
├── pom.xml               # 父POM
└── docker-compose.yml    # 基础设施（MySQL、Nacos）
```

---

## Step 2.5: 真实下单业务闭环

### 需求目标

将 mall-order 的 createOrder 从"只校验库存"升级为"真实创建订单并扣减库存"。

### 严格范围

#### 1. 扩展 ProductRpcService 接口

位置：`mall-common/src/main/java/com/mall/common/rpc/ProductRpcService.java`

新增方法：

```java
boolean deductStock(Long productId, Integer quantity, String orderNo);
boolean restoreStock(Long productId, Integer quantity, String orderNo);
```

#### 2. 修改 ProductRpcServiceImpl

位置：`mall-product/src/main/java/com/mall/product/rpc/ProductRpcServiceImpl.java`

要求：
- 注入 `ProductService`，复用库存扣减逻辑
- 新增 `deductStock`、`restoreStock` 方法直接调用 `ProductService`
- 禁止复制 ProductService 里的扣库存逻辑

#### 3. 修改 OrderService#createOrder

位置：`mall-order/src/main/java/com/mall/order/service/OrderService.java`

流程调整：
1. 校验 userId / productId / quantity 基本参数
2. 通过 `userRpcService.getUserById` 校验用户存在
3. 通过 `productRpcService.getProductById` 查询商品
4. 校验商品存在
5. 生成 orderNo
6. 计算 totalAmount = product.price * quantity（不信任前端）
7. 设置 orderNo、status=PENDING
8. 调用 `productRpcService.deductStock(productId, quantity, orderNo)`
9. **扣库存成功后**保存订单
10. **如果扣库存失败，抛出异常，不保存订单**

库存补偿逻辑（后续添加）：
```java
try {
    this.save(order);
} catch (Exception e) {
    // 补偿：回滚库存
    productRpcService.restoreStock(order.getProductId(), order.getQuantity(), orderNo);
    throw e;
}
```

#### 4. 启动脚本优化

位置：`start-services.sh`

改进：
- 按依赖顺序启动服务：mall-user → mall-product → mall-order → mall-payment → mall-admin
- `wait_http` 函数循环等待，最多60秒
- 启动失败时打印日志并 exit 1

```bash
wait_http() {
  local url=$1
  local name=$2
  local max_wait=60
  local count=0

  while [ $count -lt $max_wait ]; do
    if curl -s "${url}" > /dev/null 2>&1; then
      echo "${name} is ready!"
      return 0
    fi
    count=$((count + 2))
    sleep 2
  done

  echo "ERROR: ${name} failed to start"
  tail -120 "/tmp/mall-logs/${name}.log"
  exit 1
}
```

#### 5. 测试脚本

位置：`scripts/test-order-flow.sh`

硬验收检查项：
1. 获取商品1下单前库存 BEFORE_STOCK
2. 创建订单，断言返回 code=200
3. 提取 ORDER_NO，断言非空
4. 获取商品1下单后库存 AFTER_STOCK
5. 断言 AFTER_STOCK = BEFORE_STOCK - 1
6. 查询 mall_order.orders，确认存在 ORDER_NO
7. 查询 mall_product.inventory_logs，确认存在同一 ORDER_NO
8. 任一失败 exit 1

---

## 验收标准

| 检查项 | 标准 |
|--------|------|
| BUILD SUCCESS | mvn clean package -DskipTests 成功 |
| 5个服务启动 | mall-user, mall-product, mall-order, mall-payment, mall-admin 全部 healthy |
| 创建订单返回 | code=200 |
| 库存减少 | 商品库存正确扣减 |
| 订单创建 | mall_order.orders 有新记录 |
| 库存日志 | mall_product.inventory_logs 有记录，order_no 一致 |

---

## Git 提交记录

| Commit | 描述 |
|--------|------|
| `d16aba9` | feat: Step 2.5 稳定性修补 - 顺序启动 + 库存补偿 + 硬验收 |
| `3d27340` | feat: 更新 test-order-flow.sh 添加 MySQL inventory_logs 验证 |
| `d16aba9` | feat: Step 2.5 稳定性修补 - 顺序启动 + 库存补偿 + 硬验收 |
| `f22cfa5` | feat: Step 3 RocketMQ订单事件 (demo004) |

---

## Step 3: RocketMQ 订单事件

### 目标

mall-order 创建订单成功后，发送 OrderCreatedEvent 到 RocketMQ。
mall-admin 消费 OrderCreatedEvent，并写入 operation_logs 表。

### 实现内容

#### 1. Docker Compose 增加 RocketMQ

位置：`docker-compose.yml`

- rocketmq-namesrv: RocketMQ NameServer (端口9876)
- rocketmq-broker: RocketMQ Broker (端口10911)

#### 2. OrderCreatedEvent 事件类

位置：`mall-common/src/main/java/com/mall/common/event/OrderCreatedEvent.java`

字段：orderNo, userId, productId, quantity, totalAmount, createTime

#### 3. OrderEventProducer 事件发送

位置：`mall-order/src/main/java/com/mall/order/service/OrderEventProducer.java`

- 注入 RocketMQTemplate
- 发送 OrderCreatedEvent 到 topic: order-created-topic
- 发送失败只记录日志，不影响订单创建主流程

#### 4. OrderCreatedEventConsumer 事件消费

位置：`mall-admin/src/main/java/com/mall/admin/listener/OrderCreatedEventConsumer.java`

- 监听 topic: order-created-topic
- consumerGroup: mall-admin-order-created-consumer
- 收到事件后写入 OperationLog

#### 5. 配置

mall-order:
```yaml
rocketmq:
  name-server: localhost:9876
  producer:
    group: mall-order-producer
```

mall-admin:
```yaml
rocketmq:
  name-server: localhost:9876
```

---

## 禁止事项

- 禁止引入 Sentinel
- 禁止引入 Seata
- 禁止重构项目结构
- 禁止新建大量 DTO/VO
- 禁止改数据库表结构
- 禁止绕过 Dubbo 改成 HTTP 调用

---

## 后续步骤（待开发）

按照规划路线：

1. Step 2.5 ✓ 真实下单业务闭环 - **已完成**
2. Step 3 ✓ RocketMQ 订单事件 - **已完成**
3. Step 4: Seata 分布式事务
4. Step 5: Sentinel 限流熔断
5. Step 6: XXL-JOB 取消超时订单
6. Step 7: Redisson 防重复下单 / 分布式锁
7. Step 8: SkyWalking 链路追踪
8. Step 9: ShardingSphere 订单分库分表
9. Step 10: Arthas 故障诊断

---

## 快速验证命令

```bash
cd /workspace/2/demo003_mall

# 启动基础设施（RocketMQ）
docker compose up -d

# 手动创建topic（首次需要）
docker exec mall-rocketmq-namesrv sh mqadmin updateTopic -n localhost:9876 -t order-created-topic -c DefaultCluster -r 4 -w 4

# 编译
mvn clean package -DskipTests

# 启动服务
bash start-services.sh

# 执行测试
bash scripts/test-order-flow.sh
bash scripts/test-rocketmq-order-event.sh
```
