package com.mall.admin.listener;

import com.alibaba.fastjson2.JSON;
import com.mall.admin.service.OperationLogService;
import com.mall.common.entity.OperationLog;
import com.mall.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * Order created event consumer
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
    topic = "order-created-topic",
    consumerGroup = "mall-admin-order-created-consumer"
)
public class OrderCreatedEventConsumer implements RocketMQListener<OrderCreatedEvent> {

    private final OperationLogService operationLogService;

    @Override
    public void onMessage(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event.getOrderNo());

        OperationLog operationLog = new OperationLog();
        operationLog.setUsername("system");
        operationLog.setModule("order");
        operationLog.setOperation("ORDER_CREATED_EVENT");
        operationLog.setMethod("RocketMQ");
        operationLog.setParams(JSON.toJSONString(event));
        operationLog.setResult("consumed");
        operationLog.setStatus(1);
        operationLog.setIp("rocketmq");
        operationLog.setDuration(0L);

        operationLogService.save(operationLog);
        log.info("OrderCreatedEvent logged to operation_logs, orderNo: {}", event.getOrderNo());
    }
}
