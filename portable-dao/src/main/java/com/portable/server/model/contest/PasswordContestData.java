package com.portable.server.model.contest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PasswordContestData extends BaseContestData {

    /**
     * 密码
     */
    private String password;
}
