package com.example.service;

import com.example.common.dto.CreateOrderDTO;
import com.example.common.dto.OrderDTO;
import com.example.common.vo.OrderVO;
import com.example.common.vo.PageVO;

public interface OrderService {
    OrderVO create(CreateOrderDTO createOrderDTO);

    OrderVO getById(Long id);

    OrderVO getByOrderNo(String orderNo);

    PageVO<OrderVO> getPage(OrderDTO orderDTO);

    void cancel(Long id);

    void pay(Long id, String paymentMethod);

    void ship(Long id);

    void confirm(Long id);
}
