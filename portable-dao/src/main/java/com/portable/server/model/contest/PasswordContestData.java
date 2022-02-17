package com.portable.server.model.contest;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shiroha
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordContestData extends BasicContestData {

    /**
     * 密码
     */
    private String password;
}
