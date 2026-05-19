package com.example.service;

import com.example.common.dto.PageDTO;
import com.example.common.dto.ProductDTO;
import com.example.common.vo.PageVO;
import com.example.common.vo.ProductVO;

import java.util.List;

public interface ProductService {
    Long create(ProductDTO productDTO);

    ProductVO getById(Long id);

    PageVO<ProductVO> getPage(ProductDTO productDTO);

    List<ProductVO> getRecommendProducts();

    void update(ProductDTO productDTO);

    void delete(Long id);

    void updateStock(Long id, Integer quantity);
}
