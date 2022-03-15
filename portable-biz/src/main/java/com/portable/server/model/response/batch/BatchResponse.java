package com.portable.server.model.response.batch;

import com.portable.server.model.user.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shiroha
 */
@Data
public class BatchResponse {

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

        BatchUser(User user) {
            this.handle = user.getHandle();
            this.password = user.getPassword();
        }

        public static BatchUser of(User user) {
            return new BatchUser(user);
        }
    }

    BatchResponse(Long id) {
        this.id = id;
        this.batchUserList = new ArrayList<>();
    }

    public void add(User user) {
        this.batchUserList.add(BatchUser.of(user));
    }

    public static BatchResponse of(Long id) {
        return new BatchResponse(id);
    }
}
