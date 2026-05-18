package com.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.entity.InventoryLog;
import com.mall.common.entity.Product;
import com.mall.common.constants.InventoryChangeType;
import com.mall.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    private final InventoryLogService inventoryLogService;

    public Product createProduct(Product product) {
        this.save(product);
        return product;
    }

    public Product updateProduct(Product product) {
        this.updateById(product);
        return this.getById(product.getId());
    }

    public boolean deductStock(Long productId, Integer quantity, String orderNo) {
        Product product = this.getById(productId);
        if (product == null || product.getStock() < quantity) {
            return false;
        }

        int beforeStock = product.getStock();
        int afterStock = beforeStock - quantity;

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getId, productId)
               .ge(Product::getStock, quantity);
        Product update = new Product();
        update.setStock(afterStock);

        boolean success = this.update(update, wrapper);

        if (success) {
            InventoryLog log = new InventoryLog();
            log.setProductId(productId);
            log.setOrderNo(orderNo);
            log.setChangeType(InventoryChangeType.SELL);
            log.setBeforeStock(beforeStock);
            log.setAfterStock(afterStock);
            log.setChangeQuantity(quantity);
            log.setOperator("system");
            inventoryLogService.save(log);
        }

        return success;
    }

    public boolean restoreStock(Long productId, Integer quantity, String orderNo) {
        Product product = this.getById(productId);
        if (product == null) {
            return false;
        }

        int beforeStock = product.getStock();
        int afterStock = beforeStock + quantity;

        Product update = new Product();
        update.setId(productId);
        update.setStock(afterStock);
        this.updateById(update);

        InventoryLog log = new InventoryLog();
        log.setProductId(productId);
        log.setOrderNo(orderNo);
        log.setChangeType(InventoryChangeType.REFUND);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setChangeQuantity(quantity);
        log.setOperator("system");
        inventoryLogService.save(log);

        return true;
    }

    public List<Product> listProducts(int page, int size) {
        return this.page(new Page<>(page, size)).getRecords();
    }

    public long countProducts() {
        return this.count();
    }

    public List<Product> searchByName(String name) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Product::getProductName, name);
        return this.list(wrapper);
    }
}
