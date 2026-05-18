package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.dto.CreateOrderDTO;
import com.example.common.dto.OrderDTO;
import com.example.common.entity.Order;
import com.example.common.entity.OrderItem;
import com.example.common.entity.Product;
import com.example.common.enums.OrderStatus;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BeanCopyUtils;
import com.example.common.vo.OrderItemVO;
import com.example.common.vo.OrderVO;
import com.example.common.vo.PageVO;
import com.example.mapper.OrderItemMapper;
import com.example.mapper.OrderMapper;
import com.example.mapper.ProductMapper;
import com.example.mapper.repository.OrderItemRepository;
import com.example.mapper.repository.OrderRepository;
import com.example.service.OrderService;
import com.github.pagehelper.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           OrderMapper orderMapper,
                           OrderItemMapper orderItemMapper,
                           ProductMapper productMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO create(CreateOrderDTO createOrderDTO) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setReceiverName(createOrderDTO.getReceiverName());
        order.setReceiverPhone(createOrderDTO.getReceiverPhone());
        order.setShippingAddress(createOrderDTO.getShippingAddress());
        order.setRemark(createOrderDTO.getRemark());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setOrderTime(LocalDateTime.now());
        order.setTotalQuantity(0);
        order.setTotalAmount(BigDecimal.ZERO);

        orderRepository.insert(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderDTO.OrderItemDTO itemDTO : createOrderDTO.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: " + itemDTO.getProductId());
            }
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new BusinessException("商品库存不足: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());
            totalQuantity += itemDTO.getQuantity();

            // Deduct stock
            product.setStock(product.getStock() - itemDTO.getQuantity());
            productMapper.updateById(product);
        }

        orderItemRepository.getMapper().insertBatch(orderItems);

        order.setTotalAmount(totalAmount);
        order.setTotalQuantity(totalQuantity);
        orderRepository.update(order);

        return getById(order.getId());
    }

    @Override
    public OrderVO getById(Long id) {
        Order order = orderRepository.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
    }

    @Override
    public OrderVO getByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(
            new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
    }

    @Override
    public PageVO<OrderVO> getPage(OrderDTO orderDTO) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (orderDTO.getOrderNo() != null) {
            wrapper.like(Order::getOrderNo, orderDTO.getOrderNo());
        }
        if (orderDTO.getUserId() != null) {
            wrapper.eq(Order::getUserId, orderDTO.getUserId());
        }
        if (orderDTO.getStatus() != null) {
            wrapper.eq(Order::getStatus, orderDTO.getStatus());
        }
        if (orderDTO.getReceiverName() != null) {
            wrapper.like(Order::getReceiverName, orderDTO.getReceiverName());
        }
        if (orderDTO.getReceiverPhone() != null) {
            wrapper.like(Order::getReceiverPhone, orderDTO.getReceiverPhone());
        }

        wrapper.orderByDesc(Order::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(orderDTO.getPage(), orderDTO.getPageSize());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> result =
            orderMapper.selectPage(page, wrapper);

        PageVO<OrderVO> pageVO = new PageVO<>();
        pageVO.setTotal(result.getTotal());
        pageVO.setRecords(result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        pageVO.setPage((int) result.getCurrent());
        pageVO.setPageSize((int) result.getSize());
        pageVO.setTotalPages((int) result.getPages());
        return pageVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        Order order = orderRepository.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("只能取消待支付订单");
        }

        // Restore stock
        List<OrderItem> items = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id)
        );
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            product.setStock(product.getStock() + item.getQuantity());
            productMapper.updateById(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.update(order);
    }

    @Override
    public void pay(Long id, String paymentMethod) {
        Order order = orderRepository.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(OrderStatus.PAID);
        order.setPayTime(LocalDateTime.now());
        orderRepository.update(order);
    }

    @Override
    public void ship(Long id) {
        Order order = orderRepository.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setShipTime(LocalDateTime.now());
        orderRepository.update(order);
    }

    @Override
    public void confirm(Long id) {
        Order order = orderRepository.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        orderRepository.update(order);
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = BeanCopyUtils.copyBean(order, OrderVO.class);
        vo.setStatusDesc(order.getStatus().getDescription());

        List<OrderItem> items = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );
        vo.setItems(BeanCopyUtils.copyBeanList(items, OrderItemVO.class));
        return vo;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
