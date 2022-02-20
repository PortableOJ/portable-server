package com.portable.server.model.contest;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author shiroha
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class PrivateContestData extends BasicContestData {

    /**
     * 邀请的用户列表
     */
    private Set<Long> inviteUserSet;
}
