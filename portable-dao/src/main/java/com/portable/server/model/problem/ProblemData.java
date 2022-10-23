package com.portable.server.model.problem;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.portable.server.exception.PortableException;
import com.portable.server.model.BaseEntity;
import com.portable.server.type.JudgeCodeType;
import com.portable.server.type.LanguageType;
import com.portable.server.type.ProblemType;
import com.portable.server.type.SolutionStatusType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 题目详细信息
 *
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProblemData extends BaseEntity<String> {

    /**
     * 首次关联至的比赛 ID
     */
    private Long contestId;

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

    /**
     * 题目描述
     */
    private String description;

    /**
     * 输入描述
     */
    private String input;

    /**
     * 输出描述
     */
    private String output;

    /**
     * 输入输出样例
     */
    private List<Example> example;

    /**
     * 题目类型
     */
    private ProblemType type;

    /**
     * judge 模式
     */
    private JudgeCodeType judgeCodeType;

    /**
     * DIY judge code
     */
    private String judgeCode;

    /**
     * 测试样例的名称，允许给不同的样例准备不同的名称，输入为 XXX.in，输出为 XXX.out，交互题只有输入
     */
    private List<String> testName;

    /**
     * 是否允许下载样例
     */
    private Boolean shareTest;

    /**
     * 标准代码，必定为通过
     */
    private StdCode stdCode;

    /**
     * 测试代码（并不一定是通过，可能是故意错误的，但是一定有一份是通过的）
     */
    private List<StdCode> testCodeList;

    /**
     * 题目版本号
     */
    private Integer version;

    /**
     * 题目最后更新时间
     */
    private Date gmtModifyTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Example {

        /**
         * 样例输入（原始文件格式 \n 表示换行）
         */
        private String in;

        /**
         * 样例输出（原始文件格式 \n 表示换行）
         */
        private String out;

        /**
         * 提示
         */
        private String hint;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StdCode {

        /**
         * 文件名
         */
        private String name;

        /**
         * 代码
         */
        private String code;

        /**
         * 期望结果
         */
        private SolutionStatusType expectResultType;

        /**
         * 所使用的语言
         */
        private LanguageType languageType;

        /**
         * 最终的测试 ID
         */
        private Long solutionId;

        public void reset() {
            this.solutionId = null;
        }
    }

    public void findTest(String name) {
        if (!testName.contains(name)) {
            throw PortableException.of("A-04-006");
        }
    }

    public StdCode findStdCode(String name) {
        StdCode showStdCode = testCodeList.stream()
                .filter(stdCode -> stdCode.getName().equals(name))
                .findAny()
                .orElse(null);
        if (showStdCode == null) {
            throw PortableException.of("A-04-006");
        }
        return showStdCode;
    }

    public void nextVersion() {
        this.version++;
    }

    public Integer getTimeLimit(LanguageType languageType) {
        return specialTimeLimit == null ? defaultTimeLimit : specialTimeLimit.getOrDefault(languageType, defaultTimeLimit);
    }

    public Integer getMemoryLimit(LanguageType languageType) {
        return specialMemoryLimit == null ? defaultMemoryLimit : specialMemoryLimit.getOrDefault(languageType, defaultMemoryLimit);
    }
}
