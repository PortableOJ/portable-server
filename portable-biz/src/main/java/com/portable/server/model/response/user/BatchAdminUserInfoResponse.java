package com.portable.server.model.response.user;

import com.portable.server.model.batch.Batch;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author shiroha
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BatchAdminUserInfoResponse extends BatchUserInfoResponse {

    private List<BatchUserData.IpRecord> ipRecordList;

    BatchAdminUserInfoResponse(User user, BatchUserData userData, Batch batch) {
        super(user, batch);
        this.ipRecordList = userData.getIpList();
    }

    public static BatchAdminUserInfoResponse of(User user, BatchUserData userData, Batch batch) {
        return new BatchAdminUserInfoResponse(user, userData, batch);
    }
}
