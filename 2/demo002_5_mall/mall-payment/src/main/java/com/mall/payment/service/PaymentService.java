package com.mall.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.entity.Payment;
import com.mall.common.constants.PaymentStatus;
import com.mall.payment.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService extends ServiceImpl<PaymentMapper, Payment> {

    public Payment createPayment(Payment payment) {
        payment.setPaymentNo(generatePaymentNo());
        payment.setStatus(PaymentStatus.PENDING);
        this.save(payment);
        return payment;
    }

    public boolean simulatePay(Long paymentId) {
        Payment payment = this.getById(paymentId);
        if (payment == null || payment.getStatus() != PaymentStatus.PENDING) {
            return false;
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaidTime(java.time.LocalDateTime.now().toString());
        return this.updateById(payment);
    }

    public boolean refund(Long paymentId) {
        Payment payment = this.getById(paymentId);
        if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
            return false;
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        return this.updateById(payment);
    }

    public Payment getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderNo, orderNo);
        return this.getOne(wrapper);
    }

    public List<Payment> listByUserId(Long userId) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getUserId, userId);
        return this.list(wrapper);
    }

    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
