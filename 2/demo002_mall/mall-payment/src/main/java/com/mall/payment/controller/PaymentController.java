package com.mall.payment.controller;

import com.mall.common.entity.Payment;
import com.mall.common.response.ApiResponse;
import com.mall.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<Payment> createPayment(@Valid @RequestBody Payment payment) {
        return ApiResponse.success(paymentService.createPayment(payment));
    }

    @PostMapping("/simulate-pay/{id}")
    public ApiResponse<Boolean> simulatePay(@PathVariable Long id) {
        return ApiResponse.success(paymentService.simulatePay(id));
    }

    @PostMapping("/refund/{id}")
    public ApiResponse<Boolean> refund(@PathVariable Long id) {
        return ApiResponse.success(paymentService.refund(id));
    }

    @GetMapping("/{id}")
    public ApiResponse<Payment> getPayment(@PathVariable Long id) {
        return ApiResponse.success(paymentService.getById(id));
    }

    @GetMapping("/order/{orderNo}")
    public ApiResponse<Payment> getByOrderNo(@PathVariable String orderNo) {
        return ApiResponse.success(paymentService.getByOrderNo(orderNo));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Payment>> listByUserId(@PathVariable Long userId) {
        return ApiResponse.success(paymentService.listByUserId(userId));
    }
}
