package com.portable.server.model.contest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PublicContestData extends BaseContestData {
}
