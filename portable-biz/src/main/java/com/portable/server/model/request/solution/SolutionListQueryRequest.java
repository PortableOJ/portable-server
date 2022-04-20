package com.portable.server.model.request.solution;

import com.portable.server.type.SolutionStatusType;
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
public class SolutionListQueryRequest {

    /**
     * 仅查看此用户的提交
     */
    private String userHandle;

    /**
     * 仅查看此问题的提交
     */
    private Long problemId;

    /**
     * 仅查看此状态的提交
     */
    private SolutionStatusType statusType;

    /**
     * 限制在哪个 id 之前
     */
    private Long beforeId;

    /**
     * 限制在哪个 id 之后
     */
    private Long afterId;
}
