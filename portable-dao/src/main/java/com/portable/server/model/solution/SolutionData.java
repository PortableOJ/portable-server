package com.portable.server.model.solution;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

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
    private Map<String, String> runningMsg;

    /**
     * 运行的版本号
     */
    private Integer runOnVersion;
}
