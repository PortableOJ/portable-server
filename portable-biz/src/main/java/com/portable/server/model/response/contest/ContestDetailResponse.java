package com.portable.server.model.response.contest;

import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.response.problem.ProblemListResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @author shiroha
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestDetailResponse extends ContestInfoResponse {

    /**
     * 题目列表
     */
    private List<ProblemListResponse> problemList;

    ContestDetailResponse(Contest contest, BaseContestData contestData, String ownerHandle, List<ProblemListResponse> problemList, Set<String> coAuthor) {
        super(contest, contestData, ownerHandle, coAuthor);
        this.problemList = problemList;
    }

    public static ContestDetailResponse of(Contest contest, BaseContestData contestData, String ownerHandle, List<ProblemListResponse> problemList, Set<String> coAuthor) {
        return new ContestDetailResponse(contest, contestData, ownerHandle, problemList, coAuthor);
    }
}
