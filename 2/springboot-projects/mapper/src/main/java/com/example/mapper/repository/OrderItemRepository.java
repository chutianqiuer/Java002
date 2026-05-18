package com.example.mapper.repository;

import com.example.common.entity.OrderItem;
import com.example.mapper.OrderItemMapper;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemRepository implements Repository<OrderItem> {

    private final OrderItemMapper orderItemMapper;

    public OrderItemRepository(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public BaseMapper<OrderItem> getMapper() {
        return orderItemMapper;
    }
}
