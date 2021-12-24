package com.portable.server.model.response;

import com.portable.server.model.request.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author shiroha
 */
@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private Integer pageNum;

    private Integer pageSize;

    private Integer totalNum;

    private Integer totalPage;

    @Setter
    private List<T> data;

    public static <T, U> PageResponse<T> of(PageRequest<U> request, Integer totalNum) {
        int maxPageNum = totalNum == 0 ? 1 : (totalNum + request.getPageSize() - 1) / request.getPageSize();
        if (maxPageNum < request.getPageNum()) {
            request.setPageNum(maxPageNum);
        }
        return new PageResponse<>(request.getPageNum(),
                request.getPageSize(),
                totalNum,
                (totalNum + request.getPageNum() - 1) / request.getPageSize(),
                null);
    }

    public Integer offset() {
        return pageSize * (pageNum - 1);
    }

}
