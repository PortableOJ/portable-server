package com.portable.server.model.request.batch;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author shiroha
 */
@Data
public class BatchIpLockUpdateRequest {

    /**
     * 批量用户的 ID
     */
    @NotNull(message = "A-10-006")
    @Positive(message = "A-10-006")
    private Long id;

    /**
     * 批量用户的新锁定状态
     */
    @NotNull(message = "A-10-005")
    private Boolean ipLock;
}
