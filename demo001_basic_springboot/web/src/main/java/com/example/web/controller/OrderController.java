package com.example.web.controller;

import com.example.common.dto.CreateOrderDTO;
import com.example.common.dto.OrderDTO;
import com.example.common.vo.OrderVO;
import com.example.common.vo.PageVO;
import com.example.common.vo.Result;
import com.example.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@Tag(name = "订单管理", description = "订单创建、查询、取消、支付、发货、确认")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单，包含收货信息和商品列表")
    public Result<OrderVO> create(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        return Result.success(orderService.create(createOrderDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单信息")
    public Result<OrderVO> getById(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @GetMapping("/no/{orderNo}")
    @Operation(summary = "根据订单号查询", description = "根据订单号获取订单信息")
    public Result<OrderVO> getByOrderNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        return Result.success(orderService.getByOrderNo(orderNo));
    }

    @GetMapping("/page")
    @Operation(summary = "订单分页列表", description = "分页查询订单，支持按状态、订单号筛选")
    public Result<PageVO<OrderVO>> getPage(OrderDTO orderDTO) {
        return Result.success(orderService.getPage(orderDTO));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消待支付的订单")
    public Result<Void> cancel(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.cancel(id);
        return Result.success();
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单", description = "完成订单支付")
    public Result<Void> pay(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "支付方式") @RequestParam String paymentMethod) {
        orderService.pay(id, paymentMethod);
        return Result.success();
    }

    @PostMapping("/{id}/ship")
    @Operation(summary = "发货", description = "商家发货")
    public Result<Void> ship(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.ship(id);
        return Result.success();
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "确认收货", description = "用户确认收货")
    public Result<Void> confirm(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.confirm(id);
        return Result.success();
    }
}
