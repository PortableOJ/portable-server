package com.portable.server.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest<T> {

    /**
     * 默认页码
     */
    private static final Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 默认单页数据量
     */
    private static final Integer DEFAULT_PAGE_SIZE = 30;

    /**
     * 最大单页数量
     */
    private static final Integer MAX_PAGE_SIZE = 200;

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
        if (null == pageNum || pageNum < 1) {
            pageNum = DEFAULT_PAGE_NUM;
        }

        if (null == pageSize || pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
    }
}
