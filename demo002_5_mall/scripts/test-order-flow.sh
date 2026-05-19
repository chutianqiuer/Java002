#!/usr/bin/env bash
set -e

cd /workspace/2/demo003_mall

echo "=== Step 2.5: 真实下单业务闭环 - 硬验收 ==="
echo ""

# Check if services are running
echo "=== 检查服务状态 ==="
for svc in mall-user mall-product mall-order mall-payment mall-admin; do
  count=$(curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=${svc}" 2>/dev/null | grep -o '"healthy":true' | wc -l || echo "0")
  echo "${svc}: ${count} instance(s) healthy"
done
echo ""

# Get product 1 stock BEFORE order
echo "=== 1. 获取商品1下单前库存 ==="
BEFORE_RESPONSE=$(curl -s http://localhost:8082/api/product/1)
BEFORE_STOCK=$(echo "$BEFORE_RESPONSE" | grep -o '"stock":[0-9]*' | grep -o '[0-9]*' | head -1)
if [ -z "$BEFORE_STOCK" ]; then
  echo "ERROR: Failed to get BEFORE_STOCK"
  exit 1
fi
echo "BEFORE_STOCK: $BEFORE_STOCK"
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

# Get product 1 stock AFTER order
echo "=== 4. 获取商品1下单后库存 ==="
AFTER_RESPONSE=$(curl -s http://localhost:8082/api/product/1)
AFTER_STOCK=$(echo "$AFTER_RESPONSE" | grep -o '"stock":[0-9]*' | grep -o '[0-9]*' | head -1)
if [ -z "$AFTER_STOCK" ]; then
  echo "ERROR: Failed to get AFTER_STOCK"
  exit 1
fi
echo "AFTER_STOCK: $AFTER_STOCK"
echo ""

# Assert AFTER_STOCK = BEFORE_STOCK - 1
EXPECTED_STOCK=$((BEFORE_STOCK - 1))
if [ "$AFTER_STOCK" != "$EXPECTED_STOCK" ]; then
  echo "ERROR: Stock assertion failed"
  echo "  Expected: $EXPECTED_STOCK"
  echo "  Actual: $AFTER_STOCK"
  exit 1
fi
echo "✓ Stock correctly decreased from $BEFORE_STOCK to $AFTER_STOCK"
echo ""

# Check order exists in mall_order.orders
echo "=== 6. 验证订单存在于 mall_order.orders ==="
ORDER_LIST=$(curl -s http://localhost:8083/api/order/list)
if ! echo "$ORDER_LIST" | grep -q "$ORDER_NO"; then
  echo "ERROR: Order $ORDER_NO not found in order list"
  exit 1
fi
echo "✓ Order $ORDER_NO found in mall_order.orders"
echo ""

# Check inventory_log exists
echo "=== 7. 验证库存日志存在于 mall_product.inventory_logs ==="
LOG_CHECK=$(docker exec mall-mysql mysql -uroot -proot123 -e "
  SELECT order_no FROM mall_product.inventory_logs WHERE order_no='$ORDER_NO';" 2>/dev/null | grep "$ORDER_NO")
if [ -z "$LOG_CHECK" ]; then
  echo "ERROR: Inventory log for order $ORDER_NO not found"
  exit 1
fi
echo "✓ Inventory log for order $ORDER_NO found"
echo ""

# Show inventory log details
echo "=== 库存日志详情 ==="
docker exec mall-mysql mysql -uroot -proot123 -e "
  SELECT id, product_id, order_no, change_type, before_stock, after_stock, change_quantity
  FROM mall_product.inventory_logs
  WHERE order_no='$ORDER_NO';" 2>/dev/null
echo ""

echo "=========================================="
echo "Step 2.5 order flow verified successfully."
echo "=========================================="
