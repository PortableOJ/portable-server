package com.portable.server.model.solution;

import com.portable.server.type.SolutionStatusType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

/**
 * @author shiroha
 */
@Data
@Builder
public class SolutionData {

    /**
     * Mongo ID
     */
    @Id
    @SuppressWarnings("AlibabaAvoidStartWithDollarAndUnderLineNaming")
    private String _id;

    /**
     * 代码内容
     */
    private String code;

    /**
     * 编译信息
     */
    private String compileMsg;

    /**
     * 运行中 judge 反馈信息
     */
    private Map<String, JudgeReportMsg> runningMsg;

    /**
     * 运行的版本号
     */
    private Integer runOnVersion;

    @Data
    @Builder
    public static class JudgeReportMsg {

        /**
         * 运行结果
         */
        private SolutionStatusType statusType;

        /**
         * judge 反馈信息
         */
        private String msg;
    }
}
