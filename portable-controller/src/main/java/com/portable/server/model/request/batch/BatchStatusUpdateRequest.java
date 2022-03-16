package com.portable.server.model.request.batch;

import com.portable.server.type.BatchStatusType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author shiroha
 */
@Data
public class BatchStatusUpdateRequest {

    /**
     * 批量用户的 ID
     */
    @NotNull(message = "A-10-006")
    @Positive(message = "A-10-006")
    private Long id;

    /**
     * 批量用户的新的状态
     */
    @NotNull(message = "A-10-007")
    private BatchStatusType newStatus;
}
