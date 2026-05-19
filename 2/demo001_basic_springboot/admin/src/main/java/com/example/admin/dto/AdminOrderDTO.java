package com.example.admin.dto;

import com.example.common.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminOrderDTO extends PageDTO {
    private String orderNo;
    private Integer status;
    private String startDate;
    private String endDate;
}
