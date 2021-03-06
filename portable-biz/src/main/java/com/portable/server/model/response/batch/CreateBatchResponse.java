package com.portable.server.model.response.batch;

import com.portable.server.model.batch.Batch;
import com.portable.server.model.user.User;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shiroha
 */
@Data
public class CreateBatchResponse {

    /**
     * 批量用户 ID
     */
    private Long id;

    /**
     * 用户的账号密码
     */
    private List<BatchUser> batchUserList;

    @Data
    public static class BatchUser {

        /**
         * 用户名
         */
        private String handle;

        /**
         * 密码
         */
        private String password;

        BatchUser(@NotNull User user) {
            this.handle = user.getHandle();
            this.password = user.getPassword();
        }

        public static BatchUser of(@NotNull User user) {
            return new BatchUser(user);
        }
    }

    CreateBatchResponse(Batch batch) {
        this.id = batch.getId();
        this.batchUserList = new ArrayList<>();
    }

    public void add(@NotNull User user) {
        this.batchUserList.add(BatchUser.of(user));
    }

    public static CreateBatchResponse of(@NotNull Batch batch) {
        return new CreateBatchResponse(batch);
    }
}
