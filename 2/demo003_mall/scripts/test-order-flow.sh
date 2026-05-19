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

# Query product 1 inventory BEFORE order
echo "=== 查询商品1下单前库存 ==="
curl -s http://localhost:8082/api/product/1 | grep -o '"stock":[0-9]*'
echo ""
echo ""

# Create order
echo "=== 创建订单 ==="
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8083/api/order \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"productId":1,"quantity":1,"shippingAddress":"Beijing","receiverName":"Test","receiverPhone":"13800138000"}')
echo "$ORDER_RESPONSE"
echo ""

# Parse order no from response
ORDER_NO=$(echo "$ORDER_RESPONSE" | grep -o '"orderNo":"[^"]*"' | head -1 | sed 's/"orderNo":"//' | sed 's/"$//' || echo "")
echo "订单号: $ORDER_NO"
echo ""

# Query product 1 inventory AFTER order
echo "=== 查询商品1下单后库存 ==="
curl -s http://localhost:8082/api/product/1 | grep -o '"stock":[0-9]*'
echo ""
echo ""

# Query order list
echo "=== 查询订单列表 ==="
curl -s http://localhost:8083/api/order/list | head -c 300
echo ""
echo ""

# Query MySQL inventory_logs
echo "=== 查询 MySQL inventory_logs ==="
docker exec mall-mysql mysql -uroot -proot123 -e "
  SELECT id, product_id, order_no, change_type, before_stock, after_stock, change_quantity 
  FROM mall_product.inventory_logs 
  ORDER BY id DESC 
  LIMIT 5;" 2>/dev/null || echo "MySQL query failed"
echo ""

# Verify order_no matches
if [ -n "$ORDER_NO" ]; then
  echo "=== 验证 order_no 一致性 ==="
  LOG_ORDER_NO=$(docker exec mall-mysql mysql -uroot -proot123 -e "
    SELECT order_no FROM mall_product.inventory_logs WHERE order_no='$ORDER_NO';" 2>/dev/null | tail -1 | tr -d '[:space:]')
  if [ "$LOG_ORDER_NO" = "$ORDER_NO" ]; then
    echo "✓ order_no 匹配成功: $ORDER_NO"
  else
    echo "✗ order_no 不匹配: expected=$ORDER_NO, actual=$LOG_ORDER_NO"
  fi
fi

echo ""
echo "=== 测试完成 ==="
