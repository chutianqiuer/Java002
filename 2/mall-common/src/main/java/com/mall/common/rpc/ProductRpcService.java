package com.mall.common.rpc;

import com.mall.common.entity.Product;

/**
 * Product RPC Service
 */
public interface ProductRpcService {

    /**
     * Get product by ID
     */
    Product getProductById(Long productId);

    /**
     * Check if product has enough stock
     */
    boolean hasEnoughStock(Long productId, Integer quantity);
}
