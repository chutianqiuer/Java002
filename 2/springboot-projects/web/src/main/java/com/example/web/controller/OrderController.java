package com.example.web.controller;

import com.example.common.dto.CreateOrderDTO;
import com.example.common.dto.OrderDTO;
import com.example.common.vo.OrderVO;
import com.example.common.vo.PageVO;
import com.example.common.vo.Result;
import com.example.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        return Result.success(orderService.create(createOrderDTO));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @GetMapping("/no/{orderNo}")
    public Result<OrderVO> getByOrderNo(@PathVariable String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }

    @GetMapping("/page")
    public Result<PageVO<OrderVO>> getPage(OrderDTO orderDTO) {
        return Result.success(orderService.getPage(orderDTO));
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.success();
    }

    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, @RequestParam String paymentMethod) {
        orderService.pay(id, paymentMethod);
        return Result.success();
    }

    @PostMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        orderService.ship(id);
        return Result.success();
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirm(id);
        return Result.success();
    }
}
