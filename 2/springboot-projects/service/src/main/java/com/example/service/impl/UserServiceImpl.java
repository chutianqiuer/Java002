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
import com.example.mapper.repository.UserRepository;
import com.example.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Long register(UserDTO userDTO) {
        User existUser = userRepository.getMapper().selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, userDTO.getUsername())
        );
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = BeanCopyUtils.copyBean(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.insert(user);
    }

    @Override
    public UserVO getById(Long id) {
        User user = userRepository.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return BeanCopyUtils.copyBean(user, UserVO.class);
    }

    @Override
    public PageVO<UserVO> getPage(PageDTO pageDTO) {
        Page<User> page = new Page<>(pageDTO.getPage(), pageDTO.getPageSize());
        IPage<User> result = userRepository.getMapper().selectPage(page, null);

        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setTotal(result.getTotal());
        pageVO.setRecords(BeanCopyUtils.copyBeanList(result.getRecords(), UserVO.class));
        pageVO.setPage(result.getCurrent());
        pageVO.setPageSize(result.getSize());
        pageVO.setTotalPages((int) result.getPages());
        return pageVO;
    }

    @Override
    public void update(UserDTO userDTO) {
        User user = userRepository.getById(userDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        User updateUser = BeanCopyUtils.copyBean(userDTO, User.class);
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            updateUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepository.update(updateUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = userRepository.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userRepository.update(user);
    }
}
