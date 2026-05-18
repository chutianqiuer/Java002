package com.example.mapper.repository;

import com.example.common.entity.Product;
import com.example.mapper.ProductMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository implements Repository<Product> {

    private final ProductMapper productMapper;

    public ProductRepository(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public BaseMapper<Product> getMapper() {
        return productMapper;
    }
}
