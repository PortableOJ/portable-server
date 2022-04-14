package com.portable.server.model.request.contest;

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
