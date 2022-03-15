package com.portable.server.model.batch;

import com.portable.server.type.BatchStatusType;
import lombok.Builder;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
@Builder
public class Batch {

    /**
     * 批量用户的 id
     */
    private Long id;

    /**
     * 批量用户的拥有者
     */
    private Long owner;

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

    /**
     * 批量用户的状态
     */
    private BatchStatusType type;
}
