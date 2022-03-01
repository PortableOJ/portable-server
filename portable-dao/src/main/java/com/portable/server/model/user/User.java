package com.portable.server.model.user;

import com.portable.server.type.AccountType;
import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class User {

    /**
     * MySQL 数据库主键 ID
     */
    private Long id;

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
