package com.portable.server.model.request.problem;

import com.portable.server.model.problem.ProblemData;
import com.portable.server.type.LanguageType;
import com.portable.server.type.SolutionStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemCodeRequest {

    /**
     * 题目 ID
     */
    @NotNull(message = "A-04-001")
    @Min(value = 1, message = "A-04-001")
    private Long id;

    /**
     * 代码内容
     */
    @NotBlank(message = "A-04-027")
    @Size(min = 10, max = 65535, message = "A-04-017")
    private String code;

    /**
     * 代码语言
     */
    @NotNull(message = "A-04-016")
    private LanguageType languageType;

    /**
     * 代码名称
     */
    @NotNull(message = "A-04-013")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]{1,15}$", message = "A-04-013")
    private String codeName;

    /**
     * 期望结果
     */
    @NotNull(message = "A-04-028")
    private SolutionStatusType resultType;

    /**
     * 转为 std code 格式
     * @param stdCode 被写入的 stdCode
     */
    public void toStdCode(ProblemData.StdCode stdCode) {
        stdCode.setCode(this.code);
        stdCode.setLanguageType(languageType);
        stdCode.setName(codeName);
        stdCode.setExpectResultType(resultType);
        stdCode.reset();
    }
}
