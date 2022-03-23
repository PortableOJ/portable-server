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

    BatchUserInfoResponse(User user, Batch batch) {
        super(user);
        this.contestId = batch.getContestId();
    }

    public static BatchUserInfoResponse of(User user, Batch batch) {
        return new BatchUserInfoResponse(user, batch);
    }
}
