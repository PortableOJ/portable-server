package com.portable.server.model.request.problem;

import com.portable.server.model.problem.ProblemData;
import com.portable.server.type.LanguageType;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSettingRequest {

    /**
     * 问题的 ID
     */
    @NotNull(message = "A-04-001")
    private Long id;

    /**
     * 题目的访问权限
     */
    @NotNull(message = "A-04-020")
    private ProblemAccessType accessType;

    /**
     * 允许使用的语言类型
     */
    @NotNull(message = "A-04-021")
    @Size(min = 1, message = "A-04-021")
    private List<LanguageType> supportLanguage;

    /**
     * 默认的耗时限制，单位（s）
     */
    @NotNull(message = "A-04-022")
    @Range(min = 1, max = 30, message = "A-04-022")
    private Integer defaultTimeLimit;

    /**
     * 默认的内存限制，单位（mb）
     */
    @NotNull(message = "A-04-023")
    @Range(min = 10, max = 1024, message = "A-04-023")
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
    @NotNull(message = "A-04-024")
    private Boolean shareTest;

    /**
     * 题目类型
     */
    @NotNull(message = "A-04-025")
    private ProblemType type;

    /**
     * 将数据写入 problemData
     *
     * @param problemData 被写入的 problemData
     * @return 若出现关键的写入变化，则返回 true
     */
    public Boolean toProblemData(ProblemData problemData) {
        // support language
        boolean result = false;

        // 检查因为更改了支持的语言，导致是否可能出现某个测试代码的语言存在条件为非了
        LanguageType stdLang = problemData.getStdCode().getLanguageType();
        if (!supportLanguage.contains(stdLang)) {
            result = true;
        }

        result = result || problemData.getTestCodeList().stream()
                .anyMatch(stdCode -> !supportLanguage.contains(stdCode.getLanguageType()));

        problemData.setSupportLanguage(supportLanguage);

        // time and memory limit

        problemData.setDefaultTimeLimit(this.defaultTimeLimit);
        problemData.setDefaultMemoryLimit(this.defaultMemoryLimit);
        problemData.setSpecialTimeLimit(this.specialTimeLimit);
        problemData.setSpecialMemoryLimit(this.specialMemoryLimit);

        problemData.setShareTest(this.shareTest);

        // 题目类型校验
        if (!Objects.equals(problemData.getType(), this.type)) {
            result = true;
        }
        problemData.setType(this.type);

        return result;
    }
}
