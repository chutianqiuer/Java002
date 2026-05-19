package com.example.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageVO<T> extends BaseVO implements Serializable {
    private Long total;
    private List<T> records;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
