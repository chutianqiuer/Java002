package com.example.admin.service;

import com.example.admin.dto.AdminOrderDTO;
import com.example.admin.dto.AdminProductDTO;
import com.example.admin.dto.AdminUserDTO;
import com.example.common.vo.OrderVO;
import com.example.common.vo.PageVO;
import com.example.common.vo.ProductVO;
import com.example.common.vo.UserVO;

public interface AdminService {

    // User Management
    PageVO<UserVO> getUserPage(AdminUserDTO adminUserDTO);

    void createUser(AdminUserDTO adminUserDTO);

    void updateUser(AdminUserDTO adminUserDTO);

    void deleteUser(Long id);

    // Product Management
    PageVO<ProductVO> getProductPage(AdminProductDTO adminProductDTO);

    Long createProduct(AdminProductDTO adminProductDTO);

    void updateProduct(AdminProductDTO adminProductDTO);

    void deleteProduct(Long id);

    // Order Management
    PageVO<OrderVO> getOrderPage(AdminOrderDTO adminOrderDTO);

    void updateOrderStatus(Long id, Integer status);
}
