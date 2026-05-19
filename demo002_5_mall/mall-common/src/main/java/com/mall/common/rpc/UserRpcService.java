package com.mall.common.rpc;

import com.mall.common.entity.User;

/**
 * User RPC Service
 */
public interface UserRpcService {

    /**
     * Get user by ID
     */
    User getUserById(Long userId);

    /**
     * Check if user exists
     */
    boolean existsById(Long userId);
}
