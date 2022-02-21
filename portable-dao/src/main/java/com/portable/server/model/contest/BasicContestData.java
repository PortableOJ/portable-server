package com.portable.server.model.contest;

import com.portable.server.type.ProblemAccessType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
public abstract class BasicContestData {

    /**
     * 题目列表
     */
    private List<Long> problemList;

    /**
     * 允许此类访问权限的题目
     */
    private Set<ProblemAccessType> problemAccessTypeList;

    /**
     * 共同的出题人
     */
    private Set<Long> coAuthor;

    /**
     * 封榜时长
     */
    private Long freezeTime;

    /**
     * 公告
     */
    private String announcement;
}
