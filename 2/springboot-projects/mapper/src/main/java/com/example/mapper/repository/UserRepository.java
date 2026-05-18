package com.example.mapper.repository;

import com.example.common.entity.User;
import com.example.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements Repository<User> {

    private final UserMapper userMapper;

    public UserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public BaseMapper<User> getMapper() {
        return userMapper;
    }
}
