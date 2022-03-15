package com.portable.server.model.request.batch;

import com.portable.server.model.batch.Batch;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class BatchRequest {

    /**
     * 批量用户所属的比赛
     */
    private Long contest;

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

    public void toBatch(Batch batch) {
        batch.setContest(this.contest);
        batch.setPrefix(this.prefix);
        batch.setCount(this.count);
        batch.setIpLock(this.ipLock);
    }
}
