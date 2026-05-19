package com.mall.user.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.entity.User;
import com.mall.common.rpc.UserRpcService;
import com.mall.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(interfaceClass = UserRpcService.class)
@RequiredArgsConstructor
public class UserRpcServiceImpl implements UserRpcService {

    private final UserMapper userMapper;

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    @Override
    public boolean existsById(Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, userId);
        return userMapper.selectCount(wrapper) > 0;
    }
}
