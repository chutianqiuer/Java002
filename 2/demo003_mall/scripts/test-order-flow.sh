#!/usr/bin/env bash
set -e

cd /workspace/2/demo003_mall

echo "=== Step 2.5: 真实下单业务闭环测试 ==="
echo ""

# Check if services are running
echo "=== 检查服务状态 ==="
for svc in mall-user mall-product mall-order mall-payment mall-admin; do
  count=$(curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=${svc}" 2>/dev/null | grep -o '"healthy":true' | wc -l || echo "0")
  echo "${svc}: ${count} instance(s) healthy"
done
echo ""

# Get product list to find a product
echo "=== 查询商品列表 ==="
PRODUCT_RESPONSE=$(curl -s http://localhost:8082/api/product/list)
echo "$PRODUCT_RESPONSE" | head -c 200
echo ""
echo ""

# Extract first product id (assuming products exist)
PRODUCT_ID=$(echo "$PRODUCT_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*' || echo "1")
echo "使用商品ID: $PRODUCT_ID"
echo ""

# Get product detail
echo "=== 查询商品详情 ==="
curl -s http://localhost:8082/api/product/$PRODUCT_ID | head -c 200
echo ""
echo ""

# Get user list to find a user
echo "=== 查询用户列表 ==="
USER_RESPONSE=$(curl -s http://localhost:8081/api/user/list)
echo "$USER_RESPONSE" | head -c 200
echo ""

USER_ID=$(echo "$USER_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*' || echo "1")
echo "使用用户ID: $USER_ID"
echo ""

# Create order
echo "=== 创建订单 ==="
ORDER_REQUEST="{\"userId\":${USER_ID},\"productId\":${PRODUCT_ID},\"quantity\":1,\"shippingAddress\":\"北京市朝阳区\",\"receiverName\":\"张三\",\"receiverPhone\":\"13800138000\"}"
echo "请求: $ORDER_REQUEST"
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8083/api/order \
  -H "Content-Type: application/json" \
  -d "$ORDER_REQUEST")
echo "响应: $ORDER_RESPONSE"
echo ""

# Parse order no from response
ORDER_NO=$(echo "$ORDER_RESPONSE" | grep -o '"orderNo":"[^"]*"' | grep -o '"[^"]*"$' | tr -d '"' || echo "")
echo "订单号: $ORDER_NO"
echo ""

# Query product inventory after order
echo "=== 下单后查询商品库存 ==="
curl -s http://localhost:8082/api/product/$PRODUCT_ID | head -c 200
echo ""
echo ""

# Query order list
echo "=== 查询订单列表 ==="
curl -s http://localhost:8083/api/order/list | head -c 300
echo ""
echo ""

# Check inventory logs (via product service if available)
echo "=== 检查库存日志 ==="
echo "提示: 库存日志存储在 mall_product.inventory_logs 表中"
echo ""

echo "=== 测试完成 ==="
