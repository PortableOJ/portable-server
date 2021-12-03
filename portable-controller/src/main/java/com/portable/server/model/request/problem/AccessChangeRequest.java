package com.portable.server.model.request.problem;

import com.portable.server.type.ProblemAccessType;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class AccessChangeRequest {

    /**
     * 题目的 ID
     */
    private Long id;

    /**
     * 题目的新的访问权限
     */
    private ProblemAccessType accessType;
}
