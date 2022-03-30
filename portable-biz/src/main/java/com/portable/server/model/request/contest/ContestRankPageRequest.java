package com.portable.server.model.request.contest;

import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class ContestRankPageRequest {

    /**
     * 是否查看封榜的数据
     */
    private Boolean freeze;
}
