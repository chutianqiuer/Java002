package com.example.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDTO extends PageDTO {
    private String orderNo;
    private Long userId;
    private Integer status;
    private String startDate;
    private String endDate;
    private String receiverName;
    private String receiverPhone;
}
