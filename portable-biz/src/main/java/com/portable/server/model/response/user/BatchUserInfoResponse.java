package com.portable.server.model.response.user;

import com.portable.server.model.batch.Batch;
import com.portable.server.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author shiroha
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BatchUserInfoResponse extends BaseUserInfoResponse {

    /**
     * 绑定至的比赛 ID
     */
    private Long contestId;

    /**
     * 当前用户是否是拥有者
     */
    private Boolean isOwner;

    BatchUserInfoResponse(User user, Batch batch, Boolean isOwner) {
        super(user);
        this.contestId = batch.getContestId();
        this.isOwner = isOwner;
    }

    public static BatchUserInfoResponse of(User user, Batch batch, Boolean isOwner) {
        return new BatchUserInfoResponse(user, batch, isOwner);
    }
}
