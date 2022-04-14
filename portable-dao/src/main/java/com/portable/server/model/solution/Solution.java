package com.portable.server.model.solution;

import com.portable.server.type.LanguageType;
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solution {

    /**
     * 提交的 id
     */
    private Long id;

    /**
     * 数据 ID
     */
    private String dataId;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 提交者 id
     */
    private Long userId;

    /**
     * 问题的 id
     */
    private Long problemId;

    /**
     * 提交至比赛的 id
     */
    private Long contestId;

    /**
     * 语言
     */
    private LanguageType languageType;

    /**
     * 当前状态
     */
    private SolutionStatusType status;

    /**
     * solution 的类型
     */
    private SolutionType solutionType;

    /**
     * 耗时（ms）
     */
    private Integer timeCost;

    /**
     * 内存消耗（kb）
     */
    private Integer memoryCost;
}
