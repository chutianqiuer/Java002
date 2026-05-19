package com.example.service;

import com.example.common.dto.PageDTO;
import com.example.common.dto.UserDTO;
import com.example.common.vo.PageVO;
import com.example.common.vo.UserVO;

public interface UserService {
    Long register(UserDTO userDTO);

    UserVO getById(Long id);

    PageVO<UserVO> getPage(PageDTO pageDTO);

    void update(UserDTO userDTO);

    void delete(Long id);

    void updateStatus(Long id, Integer status);
}
