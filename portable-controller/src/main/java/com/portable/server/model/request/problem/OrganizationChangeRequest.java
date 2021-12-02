package com.portable.server.model.request.problem;

import com.portable.server.type.OrganizationType;
import lombok.Data;

@Data
public class OrganizationChangeRequest {

    /**
     * 目标用户的 ID
     */
    private Long targetId;

    /**
     * 新组织
     */
    private OrganizationType newOrganization;
}
