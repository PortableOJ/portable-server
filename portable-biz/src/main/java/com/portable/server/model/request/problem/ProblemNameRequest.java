package com.portable.server.model.request.problem;

import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class ProblemNameRequest {

    /**
     * 问题的 ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;
}
