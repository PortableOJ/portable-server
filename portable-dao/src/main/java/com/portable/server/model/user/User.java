package com.portable.server.model.user;

import com.portable.server.model.BaseEntity;
import com.portable.server.type.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity<Long> {

    /**
     * 对应的 MongoID
     */
    private String dataId;

    /**
     * 用户名
     */
    private String handle;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号类型
     */
    private AccountType type;
}
