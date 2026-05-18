package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.dto.PageDTO;
import com.example.common.dto.ProductDTO;
import com.example.common.entity.Product;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BeanCopyUtils;
import com.example.common.vo.PageVO;
import com.example.common.vo.ProductVO;
import com.example.mapper.ProductMapper;
import com.example.mapper.repository.ProductRepository;
import com.example.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Long create(ProductDTO productDTO) {
        Product product = BeanCopyUtils.copyBean(productDTO, Product.class);
        return productRepository.insert(product);
    }

    @Override
    public ProductVO getById(Long id) {
        Product product = productRepository.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return BeanCopyUtils.copyBean(product, ProductVO.class);
    }

    @Override
    public PageVO<ProductVO> getPage(ProductDTO productDTO) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (productDTO.getName() != null) {
            wrapper.like(Product::getName, productDTO.getName());
        }
        if (productDTO.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, productDTO.getCategoryId());
        }
        if (productDTO.getStatus() != null) {
            wrapper.eq(Product::getStatus, productDTO.getStatus());
        }

        wrapper.orderByDesc(Product::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                productDTO.getPage() != null ? productDTO.getPage() : 1,
                productDTO.getPageSize() != null ? productDTO.getPageSize() : 10
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
    public List<ProductVO> getRecommendProducts() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
               .orderByDesc(Product::getCreateTime)
               .last("LIMIT 10");

        List<Product> products = productMapper.selectList(wrapper);
        return BeanCopyUtils.copyBeanList(products, ProductVO.class);
    }

    @Override
    public void update(ProductDTO productDTO) {
        Product product = productRepository.getById(productDTO.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        Product updateProduct = BeanCopyUtils.copyBean(productDTO, Product.class);
        productRepository.update(updateProduct);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void updateStock(Long id, Integer quantity) {
        Product product = productRepository.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, id)
               .set(Product::getStock, product.getStock() + quantity);
        productMapper.update(null, wrapper);
    }
}
