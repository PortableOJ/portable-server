package com.portable.server.model.request.problem;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author shiroha
 */
@Data
@Builder
public class ProblemNameRequest {

    /**
     * 问题的 ID
     */
    @NotNull(message = "A-04-001")
    @Min(value = 1, message = "A-04-001")
    private Long id;

    /**
     * 名称
     */
    private String name;
}
