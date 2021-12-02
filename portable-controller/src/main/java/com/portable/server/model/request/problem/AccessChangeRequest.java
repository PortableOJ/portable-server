package com.portable.server.model.request.problem;

import com.portable.server.type.ProblemAccessType;
import lombok.Data;

@Data
public class AccessChangeRequest {
    private Long id;
    private ProblemAccessType accessType;
}
