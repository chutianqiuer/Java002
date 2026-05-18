#!/usr/bin/env bash
set -e

cd /workspace/2

pkill -f "mall-.*1.0.0.jar" || true
sleep 2

mkdir -p /tmp/mall-logs

start_service() {
  local name=$1
  local jar=$2
  local log="/tmp/mall-logs/${name}.log"

  echo "Starting ${name}..."
  nohup java -jar "${jar}" > "${log}" 2>&1 < /dev/null &
  echo $! > "/tmp/mall-logs/${name}.pid"
}

start_service mall-user mall-user/target/mall-user-1.0.0.jar
start_service mall-product mall-product/target/mall-product-1.0.0.jar
start_service mall-order mall-order/target/mall-order-1.0.0.jar
start_service mall-payment mall-payment/target/mall-payment-1.0.0.jar
start_service mall-admin mall-admin/target/mall-admin-1.0.0.jar

sleep 20

echo "=== Java processes ==="
ps aux | grep "mall-.*1.0.0.jar" | grep -v grep || true

echo ""
echo "=== Service Health ==="
echo "mall-user:"
curl -s http://localhost:8081/api/user/list | head -c 120
echo

echo "mall-product:"
curl -s http://localhost:8082/api/product/list | head -c 120
echo

echo "mall-order:"
curl -s http://localhost:8083/api/order/list | head -c 120
echo

echo "mall-payment:"
curl -s http://localhost:8084/api/payment/1 | head -c 120
echo

echo "mall-admin:"
curl -s http://localhost:8085/api/admin/health | head -c 120
echo

echo ""
echo "=== Nacos Registration ==="
for svc in mall-user mall-product mall-order mall-payment mall-admin; do
  count=$(curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=${svc}" | grep -o '"healthy":true' | wc -l)
  echo "${svc}: ${count} instance(s) healthy"
done
