package com.example.admin.dto;

import com.example.common.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUserDTO extends PageDTO {
    private String username;
    private String realName;
    private Integer status;
}
