#!/usr/bin/env bash
set -e

cd /workspace/2/demo003_mall

pkill -f "mall-.*1.0.0.jar" || true
sleep 2

mkdir -p /tmp/mall-logs

JVM_OPTS="--add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

wait_http() {
  local url=$1
  local name=$2
  local max_wait=60
  local count=0

  echo "Waiting for ${name} to be ready at ${url}..."
  while [ $count -lt $max_wait ]; do
    if curl -s "${url}" > /dev/null 2>&1; then
      echo "${name} is ready!"
      return 0
    fi
    count=$((count + 2))
    sleep 2
  done

  echo "ERROR: ${name} failed to start within ${max_wait} seconds"
  echo "=== Last 120 lines of ${name} log ==="
  tail -120 "/tmp/mall-logs/${name}.log" 2>/dev/null || true
  exit 1
}

start_service() {
  local name=$1
  local jar=$2
  local log="/tmp/mall-logs/${name}.log"

  echo "Starting ${name}..."
  nohup java $JVM_OPTS -jar "${jar}" > "${log}" 2>&1 < /dev/null &
  echo $! > "/tmp/mall-logs/${name}.pid"
}

# 1. Start mall-user first
start_service mall-user mall-user/target/mall-user-1.0.0.jar
wait_http "http://localhost:8081/api/user/list" "mall-user"

# 2. Start mall-product
start_service mall-product mall-product/target/mall-product-1.0.0.jar
wait_http "http://localhost:8082/api/product/list" "mall-product"

# 3. Start mall-order
start_service mall-order mall-order/target/mall-order-1.0.0.jar
wait_http "http://localhost:8083/api/order/list" "mall-order"

# 4. Start mall-payment
start_service mall-payment mall-payment/target/mall-payment-1.0.0.jar
wait_http "http://localhost:8084/api/payment/1" "mall-payment"

# 5. Start mall-admin
start_service mall-admin mall-admin/target/mall-admin-1.0.0.jar
wait_http "http://localhost:8085/api/admin/health" "mall-admin"

echo ""
echo "=== Java processes ==="
ps aux | grep "mall-.*1.0.0.jar" | grep -v grep || true

echo ""
echo "=== Nacos Registration ==="
for svc in mall-user mall-product mall-order mall-payment mall-admin; do
  count=$(curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=${svc}" | grep -o '"healthy":true' | wc -l)
  echo "${svc}: ${count} instance(s) healthy"
done
