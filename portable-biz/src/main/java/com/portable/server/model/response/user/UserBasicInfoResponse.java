package com.portable.server.model.response.user;

import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户基本信息类
 *
 * @author shiroha
 */
@Getter
@Setter
public abstract class UserBasicInfoResponse {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String handle;

    /**
     * 用户账号类型
     */
    private AccountType type;

    public UserBasicInfoResponse(User user) {
        this.id = user.getId();
        this.handle = user.getHandle();
        this.type = user.getType();
    }
}
