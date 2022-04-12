package com.portable.server.model.request.contest;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author shiroha
 */
@Data
@Builder
public class ContestAuth {

    /**
     * 比赛的 id
     */
    @NotNull(message = "A-08-002")
    @Min(value = 1, message = "A-08-002")
    private Long contestId;

    /**
     * 比赛的密码
     */
    private String password;
}
