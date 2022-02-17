package com.portable.server.model.contest;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author shiroha
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrivateContestData extends BasicContestData {

    /**
     * 邀请的用户列表
     */
    private Set<Long> inviteUserSet;
}
