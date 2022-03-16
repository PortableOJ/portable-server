package com.portable.server.model.contest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BatchContestData extends BaseContestData {

    /**
     * 绑定至的批量用户组
     */
    private Long batchId;
}
