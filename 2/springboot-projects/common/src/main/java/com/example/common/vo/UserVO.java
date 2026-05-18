package com.example.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO extends BaseVO {
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Integer sex;
    private LocalDateTime createTime;
}
