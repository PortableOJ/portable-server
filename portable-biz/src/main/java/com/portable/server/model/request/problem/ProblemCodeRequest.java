package com.portable.server.model.request.problem;

import com.portable.server.model.problem.ProblemData;
import com.portable.server.type.LanguageType;
import com.portable.server.type.SolutionStatusType;
import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class ProblemCodeRequest {

    /**
     * 题目 ID
     */
    private Long id;

    /**
     * 代码内容
     */
    private String code;

    /**
     * 代码语言
     */
    private LanguageType languageType;

    /**
     * 代码名称
     */
    private String codeName;

    /**
     * 期望结果
     */
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
