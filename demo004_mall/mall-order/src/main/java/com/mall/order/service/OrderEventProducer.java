package com.mall.order.service;

import com.mall.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

/**
 * Order event producer for RocketMQ
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String ORDER_CREATED_TOPIC = "order-created-topic";

    private final RocketMQTemplate rocketMQTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            rocketMQTemplate.convertAndSend(ORDER_CREATED_TOPIC, event);
            log.info("OrderCreatedEvent sent successfully, orderNo: {}", event.getOrderNo());
        } catch (Exception e) {
            log.error("Failed to send OrderCreatedEvent, orderNo: {}", event.getOrderNo(), e);
        }
    }
}
