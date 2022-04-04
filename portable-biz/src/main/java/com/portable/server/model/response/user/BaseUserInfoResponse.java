package com.portable.server.model.response.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户基本信息类
 *
 * @author shiroha
 */
@Data
@NoArgsConstructor
public abstract class BaseUserInfoResponse {

    /**
     * 用户 id
     */
    @JsonIgnore
    private Long id;

    /**
     * 用户昵称
     */
    private String handle;

    /**
     * 用户账号类型
     */
    private AccountType type;

    BaseUserInfoResponse(User user) {
        this.id = user.getId();
        this.handle = user.getHandle();
        this.type = user.getType();
    }
}
