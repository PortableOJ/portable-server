package com.portable.server.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shiroha
 */
@Data
public class NameRequest {

    /**
     * 名称
     */
    @NotNull(message = "A-00-003")
    private String name;
}
