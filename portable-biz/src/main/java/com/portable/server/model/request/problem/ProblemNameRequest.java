package com.portable.server.model.request.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
