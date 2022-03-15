package com.portable.server.model.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BatchUserData extends BaseUserData {

    /**
     * 记录的 IP 地址，不超过 10 次，只记录变化
     */
    private List<IpRecord> ipList;

    @Data
    @Builder
    public static class IpRecord {

        /**
         * IP 的值
         */
        String ip;

        /**
         * 时间
         */
        Date date;
    }
}
