package com.mall.order.controller;

import com.mall.common.entity.Order;
import com.mall.common.response.ApiResponse;
import com.mall.common.response.PageResponse;
import com.mall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<Order> createOrder(@Valid @RequestBody Order order) {
        return ApiResponse.success(orderService.createOrder(order));
    }

    @PutMapping("/cancel/{id}")
    public ApiResponse<Boolean> cancelOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.cancelOrder(id));
    }

    @PutMapping("/status/{id}")
    public ApiResponse<Boolean> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam int status) {
        return ApiResponse.success(orderService.updateOrderStatus(id, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.getById(id));
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Order>> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResponse.of(
                orderService.countOrders(),
                page,
                size,
                orderService.listOrders(page, size)
        ));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Order>> listByUserId(@PathVariable Long userId) {
        return ApiResponse.success(orderService.listByUserId(userId));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<Order>> listByStatus(@PathVariable int status) {
        return ApiResponse.success(orderService.listByStatus(status));
    }
}
