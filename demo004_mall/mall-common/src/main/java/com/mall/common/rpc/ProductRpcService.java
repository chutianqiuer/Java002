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

    /**
     * Deduct stock for an order
     * @return true if stock was successfully deducted
     */
    boolean deductStock(Long productId, Integer quantity, String orderNo);

    /**
     * Restore stock for an order (e.g., order cancelled)
     * @return true if stock was successfully restored
     */
    boolean restoreStock(Long productId, Integer quantity, String orderNo);
}
