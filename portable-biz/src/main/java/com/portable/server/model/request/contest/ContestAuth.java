package com.portable.server.model.request.contest;

import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class ContestAuth {

    /**
     * 比赛的 id
     */
    private Long contestId;

    /**
     * 比赛的密码
     */
    private String password;
}
