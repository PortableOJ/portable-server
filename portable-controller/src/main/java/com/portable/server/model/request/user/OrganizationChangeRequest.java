package com.portable.server.model.request.user;

import com.portable.server.type.OrganizationType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author shiroha
 */
@Data
public class OrganizationChangeRequest {

    /**
     * 目标用户的 ID
     */
    @Positive(message = "A-01-001")
    private Long targetId;

    /**
     * 新组织
     */
    @NotNull(message = "A-01-008")
    private OrganizationType newOrganization;
}
