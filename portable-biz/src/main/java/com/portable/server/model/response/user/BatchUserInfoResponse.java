package com.portable.server.model.response.user;

import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shiroha
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BatchUserInfoResponse extends UserBasicInfoResponse {

    /**
     * 绑定至的比赛 ID
     */
    private Long contestId;

    BatchUserInfoResponse(User user, BatchUserData userData) {
        super(user);
        this.contestId = userData.getBatchId();
    }

    public static BatchUserInfoResponse of(User user, BatchUserData userData) {
        return new BatchUserInfoResponse(user, userData);
    }
}
