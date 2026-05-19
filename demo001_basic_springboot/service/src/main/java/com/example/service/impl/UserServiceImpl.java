package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.PageDTO;
import com.example.common.dto.UserDTO;
import com.example.common.entity.User;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BeanCopyUtils;
import com.example.common.vo.PageVO;
import com.example.common.vo.UserVO;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Long register(UserDTO userDTO) {
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, userDTO.getUsername())
        );
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = BeanCopyUtils.copyBean(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return BeanCopyUtils.copyBean(user, UserVO.class);
    }

    @Override
    public PageVO<UserVO> getPage(PageDTO pageDTO) {
        Page<User> page = new Page<>(pageDTO.getPage(), pageDTO.getPageSize());
        IPage<User> result = userMapper.selectPage(page, null);

        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setTotal(result.getTotal());
        pageVO.setRecords(BeanCopyUtils.copyBeanList(result.getRecords(), UserVO.class));
        pageVO.setPage((int) result.getCurrent());
        pageVO.setPageSize((int) result.getSize());
        pageVO.setTotalPages((int) result.getPages());
        return pageVO;
    }

    @Override
    public void update(UserDTO userDTO) {
        User user = userMapper.selectById(userDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        User updateUser = BeanCopyUtils.copyBean(userDTO, User.class);
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            updateUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userMapper.updateById(updateUser);
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }
}
