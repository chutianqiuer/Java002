package com.mall.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.entity.Order;
import com.mall.common.constants.OrderStatus;
import com.mall.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    public Order createOrder(Order order) {
        order.setOrderNo(generateOrderNo());
        order.setStatus(OrderStatus.PENDING);
        this.save(order);
        return order;
    }

    public boolean cancelOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            return false;
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            return false;
        }
        order.setStatus(OrderStatus.CANCELLED);
        return this.updateById(order);
    }

    public boolean updateOrderStatus(Long orderId, int status) {
        Order order = this.getById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        return this.updateById(order);
    }

    public List<Order> listOrders(int page, int size) {
        return this.page(new Page<>(page, size)).getRecords();
    }

    public long countOrders() {
        return this.count();
    }

    public List<Order> listByUserId(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        return this.list(wrapper);
    }

    public List<Order> listByStatus(int status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, status);
        return this.list(wrapper);
    }

    public List<Order> listExpiredPendingOrders(int minutes) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, OrderStatus.PENDING)
               .le(Order::getCreateTime, LocalDateTime.now().minusMinutes(minutes));
        return this.list(wrapper);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
