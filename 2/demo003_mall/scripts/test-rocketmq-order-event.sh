#!/usr/bin/env bash
set -e

cd /workspace/2/demo003_mall

echo "=== Step 3: RocketMQ Order Event Verification ==="
echo ""

# Check RocketMQ containers
echo "=== 1. 检查 RocketMQ 容器 ==="
NAMESRV_RUNNING=$(docker ps --filter "name=mall-rocketmq-namesrv" --filter "status=running" -q 2>/dev/null)
BROKER_RUNNING=$(docker ps --filter "name=mall-rocketmq-broker" --filter "status=running" -q 2>/dev/null)

if [ -z "$NAMESRV_RUNNING" ]; then
  echo "ERROR: RocketMQ namesrv container is not running"
  exit 1
fi
echo "✓ RocketMQ namesrv is running"

if [ -z "$BROKER_RUNNING" ]; then
  echo "ERROR: RocketMQ broker container is not running"
  exit 1
fi
echo "✓ RocketMQ broker is running"
echo ""

# Create order
echo "=== 2. 创建订单 ==="
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8083/api/order \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productId":1,"quantity":1,"shippingAddress":"Beijing","receiverName":"Test","receiverPhone":"13800138000"}')
echo "$ORDER_RESPONSE"

# Check code=200
if ! echo "$ORDER_RESPONSE" | grep -q '"code":200'; then
  echo "ERROR: Order creation did not return code=200"
  exit 1
fi
echo "✓ Order creation returned code=200"
echo ""

# Extract ORDER_NO
ORDER_NO=$(echo "$ORDER_RESPONSE" | grep -o '"orderNo":"[^"]*"' | head -1 | sed 's/"orderNo":"//;s/"$//')
if [ -z "$ORDER_NO" ]; then
  echo "ERROR: Failed to extract ORDER_NO"
  exit 1
fi
echo "ORDER_NO: $ORDER_NO"
echo ""

# Wait for consumer to process
echo "=== 3. 等待 3 秒让消费者处理 ==="
sleep 3
echo ""

# Check operation_logs in mall_admin database
echo "=== 4. 查询 mall_admin.operation_logs ==="
LOG_CHECK=$(docker exec mall-mysql mysql -uroot -proot123 -e "
  SELECT id, username, module, operation, method, params, result, status
  FROM mall_admin.operation_logs
  WHERE operation='ORDER_CREATED_EVENT'
  ORDER BY id DESC
  LIMIT 5;" 2>/dev/null)
echo "$LOG_CHECK"
echo ""

# Verify ORDER_NO in logs
if [ -n "$ORDER_NO" ]; then
  echo "=== 5. 验证 ORDER_NO 一致性 ==="
  LOG_ORDER_NO=$(echo "$LOG_CHECK" | grep "$ORDER_NO" | wc -l)
  if [ "$LOG_ORDER_NO" -gt 0 ]; then
    echo "✓ Found ORDER_CREATED_EVENT with ORDER_NO: $ORDER_NO"
  else
    echo "ERROR: ORDER_CREATED_EVENT with ORDER_NO $ORDER_NO not found in operation_logs"
    echo ""
    echo "=== Last 50 lines of mall-admin log ==="
    tail -50 /tmp/mall-logs/mall-admin.log 2>/dev/null || true
    exit 1
  fi
fi

echo ""
echo "============================================"
echo "Step 3 RocketMQ order event verified successfully."
echo "============================================"
