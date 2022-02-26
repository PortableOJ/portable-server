package com.portable.server.model.contest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PrivateContestData extends BaseContestData {

    /**
     * 邀请的用户列表
     */
    private Set<Long> inviteUserSet;
}
