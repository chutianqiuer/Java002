package com.mall.product.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.entity.Product;
import com.mall.common.rpc.ProductRpcService;
import com.mall.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(interfaceClass = ProductRpcService.class)
@RequiredArgsConstructor
public class ProductRpcServiceImpl implements ProductRpcService {

    private final ProductMapper productMapper;

    @Override
    public Product getProductById(Long productId) {
        if (productId == null) {
            return null;
        }
        return productMapper.selectById(productId);
    }

    @Override
    public boolean hasEnoughStock(Long productId, Integer quantity) {
        if (productId == null || quantity == null) {
            return false;
        }
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getId, productId)
               .ge(Product::getStock, quantity);
        return productMapper.selectCount(wrapper) > 0;
    }
}
