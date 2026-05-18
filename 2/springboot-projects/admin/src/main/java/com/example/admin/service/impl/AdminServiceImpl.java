package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.admin.dto.AdminOrderDTO;
import com.example.admin.dto.AdminProductDTO;
import com.example.admin.dto.AdminUserDTO;
import com.example.common.entity.Product;
import com.example.common.entity.User;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BeanCopyUtils;
import com.example.common.vo.OrderVO;
import com.example.common.vo.PageVO;
import com.example.common.vo.ProductVO;
import com.example.common.vo.UserVO;
import com.example.mapper.OrderMapper;
import com.example.mapper.ProductMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.repository.ProductRepository;
import com.example.mapper.repository.UserRepository;
import com.example.service.OrderService;
import com.example.admin.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    public AdminServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           ProductRepository productRepository,
                           ProductMapper productMapper,
                           OrderMapper orderMapper,
                           OrderService orderService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
        this.orderService = orderService;
    }

    @Override
    public PageVO<UserVO> getUserPage(AdminUserDTO adminUserDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (adminUserDTO.getUsername() != null) {
            wrapper.like(User::getUsername, adminUserDTO.getUsername());
        }
        if (adminUserDTO.getRealName() != null) {
            wrapper.like(User::getRealName, adminUserDTO.getRealName());
        }
        if (adminUserDTO.getStatus() != null) {
            wrapper.eq(User::getStatus, adminUserDTO.getStatus());
        }

        wrapper.orderByDesc(User::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                adminUserDTO.getPage() != null ? adminUserDTO.getPage() : 1,
                adminUserDTO.getPageSize() != null ? adminUserDTO.getPageSize() : 10
            );
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> result =
            userMapper.selectPage(page, wrapper);

        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setTotal(result.getTotal());
        pageVO.setRecords(BeanCopyUtils.copyBeanList(result.getRecords(), UserVO.class));
        pageVO.setPage((int) result.getCurrent());
        pageVO.setPageSize((int) result.getSize());
        pageVO.setTotalPages((int) result.getPages());
        return pageVO;
    }

    @Override
    public void createUser(AdminUserDTO adminUserDTO) {
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, adminUserDTO.getUsername())
        );
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = BeanCopyUtils.copyBean(adminUserDTO, User.class);
        user.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH"); // default password
        userRepository.insert(user);
    }

    @Override
    public void updateUser(AdminUserDTO adminUserDTO) {
        User user = userRepository.getById(adminUserDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        User updateUser = BeanCopyUtils.copyBean(adminUserDTO, User.class);
        userRepository.update(updateUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public PageVO<ProductVO> getProductPage(AdminProductDTO adminProductDTO) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (adminProductDTO.getName() != null) {
            wrapper.like(Product::getName, adminProductDTO.getName());
        }
        if (adminProductDTO.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, adminProductDTO.getCategoryId());
        }
        if (adminProductDTO.getStatus() != null) {
            wrapper.eq(Product::getStatus, adminProductDTO.getStatus());
        }

        wrapper.orderByDesc(Product::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                adminProductDTO.getPage() != null ? adminProductDTO.getPage() : 1,
                adminProductDTO.getPageSize() != null ? adminProductDTO.getPageSize() : 10
            );
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> result =
            productMapper.selectPage(page, wrapper);

        PageVO<ProductVO> pageVO = new PageVO<>();
        pageVO.setTotal(result.getTotal());
        pageVO.setRecords(BeanCopyUtils.copyBeanList(result.getRecords(), ProductVO.class));
        pageVO.setPage((int) result.getCurrent());
        pageVO.setPageSize((int) result.getSize());
        pageVO.setTotalPages((int) result.getPages());
        return pageVO;
    }

    @Override
    public Long createProduct(AdminProductDTO adminProductDTO) {
        Product product = BeanCopyUtils.copyBean(adminProductDTO, Product.class);
        return productRepository.insert(product);
    }

    @Override
    public void updateProduct(AdminProductDTO adminProductDTO) {
        Product product = productRepository.getById(adminProductDTO.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        Product updateProduct = BeanCopyUtils.copyBean(adminProductDTO, Product.class);
        productRepository.update(updateProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public PageVO<OrderVO> getOrderPage(AdminOrderDTO adminOrderDTO) {
        return orderService.getPage(new com.example.common.dto.OrderDTO());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long id, Integer status) {
        com.example.common.entity.Order order = new com.example.common.entity.Order();
        order.setId(id);
        order.setStatus(com.example.common.enums.OrderStatus.values()[status]);

        switch (status) {
            case 3: // SHIPPED
                order.setShipTime(java.time.LocalDateTime.now());
                break;
            case 4: // COMPLETED
                order.setCompleteTime(java.time.LocalDateTime.now());
                break;
            case 5: // CANCELLED
                break;
            default:
                break;
        }

        orderMapper.updateById(order);
    }
}
