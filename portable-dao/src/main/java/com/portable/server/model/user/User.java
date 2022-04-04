package com.portable.server.model.user;

import com.portable.server.type.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
