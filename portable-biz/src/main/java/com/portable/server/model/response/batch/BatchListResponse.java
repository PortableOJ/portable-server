package com.portable.server.model.response.batch;

import com.portable.server.model.batch.Batch;
import com.portable.server.model.contest.Contest;
import com.portable.server.type.BatchStatusType;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class BatchListResponse {

    /**
     * 批量用户的 id
     */
    private Long id;

    /**
     * 批量用户所属的比赛
     */
    private String contestTitle;

    /**
     * 批量用户的前缀词
     */
    private String prefix;

    /**
     * 批量用户的数量
     */
    private Integer count;

    /**
     * 是否进行 ip 锁
     */
    private Boolean ipLock;

    /**
     * 批量用户的状态
     */
    private BatchStatusType type;

    BatchListResponse(Batch batch, Contest contest) {
        this.id = batch.getId();
        this.contestTitle = contest == null ? "暂无锁定比赛" : contest.getTitle();
        this.prefix = batch.getPrefix();
        this.count = batch.getCount();
        this.ipLock = batch.getIpLock();
        this.type = batch.getStatus();
    }

    public static BatchListResponse of(Batch batch, Contest contest) {
        return new BatchListResponse(batch, contest);
    }
}
