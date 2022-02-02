package com.portable.server.model.response.problem;

import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.LanguageType;
import com.portable.server.type.ProblemType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProblemSettingResponse {

    /**
     * 问题的 ID
     */
    private Long id;

    /**
     * 题目类型
     */
    private ProblemType type;

    /**
     * 使用的 judge 模式
     */
    private JudgeCodeType judgeCodeType;

    /**
     * 是否允许下载样例
     */
    private Boolean shareTest;

    /**
     * 默认的耗时限制，单位（s）
     */
    private Integer defaultTimeLimit;

    /**
     * 默认的内存限制，单位（mb）
     */
    private Integer defaultMemoryLimit;

    /**
     * 部分语言的特殊时间限制
     */
    private Map<LanguageType, Integer> specialTimeLimit;

    /**
     * 部分语言的特殊内存限制
     */
    private Map<LanguageType, Integer> specialMemoryLimit;

    /**
     * 允许使用的语言类型
     */
    private List<LanguageType> supportLanguage;
}
