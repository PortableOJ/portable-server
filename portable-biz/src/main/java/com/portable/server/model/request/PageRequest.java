package com.portable.server.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageRequest<T> {

    private static final Integer DEFAULT_PAGE_NUM = 1;
    private static final Integer DEFAULT_PAGE_SIZE = 30;

    /**
     * 当前页码（1开始）
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    private T queryData;

    public void verify() {
        if (null == pageNum || pageNum < DEFAULT_PAGE_NUM) {
            pageNum = DEFAULT_PAGE_NUM;
        }

        if (null == pageSize || pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
    }
}
