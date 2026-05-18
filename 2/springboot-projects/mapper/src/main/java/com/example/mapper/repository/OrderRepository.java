package com.example.mapper.repository;

import com.example.common.entity.Order;
import com.example.mapper.OrderMapper;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository implements Repository<Order> {

    private final OrderMapper orderMapper;

    public OrderRepository(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public BaseMapper<Order> getMapper() {
        return orderMapper;
    }
}
