package com.mall.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.entity.Order;
import com.mall.common.entity.Product;
import com.mall.common.entity.User;
import com.mall.common.constants.OrderStatus;
import com.mall.common.rpc.ProductRpcService;
import com.mall.common.rpc.UserRpcService;
import com.mall.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @DubboReference(check = false)
    private UserRpcService userRpcService;

    @DubboReference(check = false)
    private ProductRpcService productRpcService;

    public Order createOrder(Order order) {
        // Validate user exists via Dubbo RPC
        User user = userRpcService.getUserById(order.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found, userId: " + order.getUserId());
        }

        // Validate product exists and has enough stock via Dubbo RPC
        Product product = productRpcService.getProductById(order.getProductId());
        if (product == null) {
            throw new RuntimeException("Product not found, productId: " + order.getProductId());
        }

        boolean hasStock = productRpcService.hasEnoughStock(order.getProductId(), order.getQuantity());
        if (!hasStock) {
            throw new RuntimeException("Insufficient stock, productId: " + order.getProductId());
        }

        // Generate orderNo first
        String orderNo = generateOrderNo();

        // Deduct stock before saving order
        boolean stockDeducted = productRpcService.deductStock(order.getProductId(), order.getQuantity(), orderNo);
        if (!stockDeducted) {
            throw new RuntimeException("Failed to deduct stock, productId: " + order.getProductId());
        }

        // Calculate total amount (do not trust frontend value)
        order.setTotalAmount(product.getPrice().multiply(java.math.BigDecimal.valueOf(order.getQuantity())));
        order.setOrderNo(orderNo);
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
