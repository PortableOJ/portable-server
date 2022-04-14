package com.portable.server.model.request.contest;

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
public class ContestRankPageRequest {

    /**
     * 是否查看封榜的数据
     */
    private Boolean freeze;
}
