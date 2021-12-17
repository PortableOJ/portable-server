package com.portable.server.model.request.problem;

import com.portable.server.model.problem.ProblemData;
import com.portable.server.type.LanguageType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemType;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author shiroha
 */
@Data
public class ProblemSettingRequest {

    /**
     * 问题的 ID
     */
    private Long id;

    /**
     * 题目的访问权限
     */
    private ProblemAccessType accessType;

    /**
     * 允许使用的语言类型
     */
    private List<LanguageType> supportLanguage;

    /**
     * 默认的耗时限制，单位（ms）
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
     * 是否允许下载样例
     */
    private Boolean shareTest;

    /**
     * 题目类型
     */
    private ProblemType type;

    /**
     * 将数据写入 problemData
     *
     * @param problemData 被写入的 problemData
     * @return 若出现关键的写入变化，则返回 true
     */
    public Boolean toProblemData(ProblemData problemData) {

        // support language
        boolean result = problemData.getSupportLanguage().containsAll(supportLanguage) && !supportLanguage.containsAll(problemData.getSupportLanguage());
        problemData.setSupportLanguage(supportLanguage);

        // time and memory limit

        problemData.setDefaultTimeLimit(this.defaultTimeLimit);
        problemData.setDefaultMemoryLimit(this.defaultMemoryLimit);
        problemData.setSpecialTimeLimit(this.specialTimeLimit);
        problemData.setSpecialMemoryLimit(this.specialMemoryLimit);

        problemData.setShareTest(this.shareTest);

        // type
        if (!Objects.equals(problemData.getType(), this.type)) {
            result = true;
        }
        problemData.setType(this.type);

        return result;
    }
}
