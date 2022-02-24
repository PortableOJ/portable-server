package com.portable.server.model.response.contest;

import com.portable.server.model.contest.BasicContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.response.problem.ProblemListResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * @author shiroha
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ContestAdminDetailResponse extends ContestDetailResponse {

    /**
     * 访问权限锁定值
     */
    private Object accessValue;

    /**
     * 题目是否是锁定的
     */
    private List<Boolean> problemLock;

    ContestAdminDetailResponse(Contest contest, BasicContestData contestData, String ownerHandle, List<ProblemListResponse> problemList, Set<String> coAuthor) {
        super(contest, contestData, ownerHandle, problemList, coAuthor);
    }
}
