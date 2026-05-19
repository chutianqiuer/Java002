package com.example.service;

import com.example.common.dto.LoginDTO;
import com.example.common.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO loginDTO);

    void logout();

    LoginVO getCurrentUser();
}
