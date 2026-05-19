package com.mall.common.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Paginated response
 */
@Data
public class PageResponse<T> implements Serializable {

    private long total;
    private long page;
    private long size;
    private List<T> records;

    public static <T> PageResponse<T> of(long total, long page, long size, List<T> records) {
        PageResponse<T> response = new PageResponse<>();
        response.setTotal(total);
        response.setPage(page);
        response.setSize(size);
        response.setRecords(records);
        return response;
    }
}
