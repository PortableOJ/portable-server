package com.portable.server.model.problem;

import com.portable.server.exception.PortableException;
import com.portable.server.type.ProblemAccessType;
import com.portable.server.type.ProblemStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

    /**
     * 题目的 ID
     */
    private Long id;

    /**
     * 题目的 dataId
     */
    private String dataId;

    /**
     * 题目的标题
     */
    private String title;

    /**
     * 题目的状态
     */
    private ProblemStatusType statusType;

    /**
     * 题目的访问权限
     */
    private ProblemAccessType accessType;

    /**
     * 历史提交数量
     */
    private Integer submissionCount;

    /**
     * 历史通过的数量
     */
    private Integer acceptCount;

    /**
     * 作者
     */
    private Long owner;

    /**
     * 进入未检查态
     * @throws PortableException 出错则返回
     */
    public void toUncheck() throws PortableException {
        this.statusType = this.statusType.toUncheck();
    }

    /**
     * 进入未处理态
     * @throws PortableException 出错则返回
     */
    public void toUntreated() throws PortableException {
        this.statusType = this.statusType.toUntreated();
    }
}
