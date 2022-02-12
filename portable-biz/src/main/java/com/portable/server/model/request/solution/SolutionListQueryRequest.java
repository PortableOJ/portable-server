package com.portable.server.model.request.solution;

import com.portable.server.type.SolutionStatusType;
import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class SolutionListQueryRequest {

    /**
     * 仅查看此用户的提交
     */
    private Long userId;

    /**
     * 仅查看此问题的提交
     */
    private Long problemId;

    /**
     * 仅查看此状态的提交
     */
    private SolutionStatusType statusType;
}
