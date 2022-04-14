package com.portable.server.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BatchUserData extends BaseUserData {

    /**
     * 记录的 IP 地址，不超过 10 次，只记录变化
     */
    private List<IpRecord> ipList;

    /**
     * 批量用户组的 ID
     */
    private Long batchId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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

    public Boolean addIpRecord(String ip) {
        if (this.ipList == null) {
            this.ipList = new ArrayList<>();
        }
        if (!this.ipList.isEmpty() && Objects.equals(this.ipList.get(this.ipList.size() - 1).ip, ip)) {
            return false;
        }
        this.ipList.add(IpRecord.builder()
                .ip(ip)
                .date(new Date())
                .build());
        return true;
    }
}
