package com.portable.server.model.batch;

import com.portable.server.model.BaseEntity;
import com.portable.server.type.BatchStatusType;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Batch extends BaseEntity<Long> {

    /**
     * 批量用户的拥有者
     */
    private Long owner;

    /**
     * 批量用户所属的比赛
     */
    private Long contestId;

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
    private BatchStatusType status;
}
