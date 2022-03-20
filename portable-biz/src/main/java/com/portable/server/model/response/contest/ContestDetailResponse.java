package com.portable.server.model.response.contest;

import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.user.User;
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

    ContestDetailResponse(Contest contest, BaseContestData contestData, User user, List<ProblemListResponse> problemList, Set<User> coAuthor) {
        super(contest, contestData, user, coAuthor);
        this.problemList = problemList;
    }

    public static ContestDetailResponse of(Contest contest, BaseContestData contestData, User user, List<ProblemListResponse> problemList, Set<User> coAuthor) {
        return new ContestDetailResponse(contest, contestData, user, problemList, coAuthor);
    }
}
