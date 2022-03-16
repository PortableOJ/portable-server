package com.portable.server.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author shiroha
 */
@Data
public class IdRequest {

    /**
     * 请求的目标 ID
     */
    @NotNull(message = "A-00-003")
    @Positive(message = "A-00-003")
    private Long id;
}
