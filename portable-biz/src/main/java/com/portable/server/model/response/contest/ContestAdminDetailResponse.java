package com.portable.server.model.response.contest;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.response.problem.ProblemListResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ContestAdminDetailResponse extends ContestDetailResponse {

    /**
     * 访问权限的配置密码
     */
    private String password;

    /**
     * 访问权限的配置邀请用户
     */
    private Set<String> inviteUserSet;

    /**
     * 批量用户组 ID
     */
    private Long batchId;

    /**
     * 题目是否是锁定的
     */
    private List<Boolean> problemLock;

    /**
     * 题目的真实 id
     */
    private List<Long> problemRealId;

    ContestAdminDetailResponse(Contest contest,
                               BaseContestData contestData,
                               String ownerHandle,
                               List<ProblemListResponse> problemList,
                               Set<String> coAuthor,
                               List<Boolean> problemLock,
                               Set<String> inviteUserSet) throws PortableException {
        super(contest, contestData, ownerHandle, problemList, coAuthor);
        switch (contest.getAccessType()) {
            case PUBLIC:
                break;
            case PASSWORD:
                PasswordContestData passwordContestData = (PasswordContestData) contestData;
                this.password = passwordContestData.getPassword();
                break;
            case PRIVATE:
                this.inviteUserSet = inviteUserSet;
                break;
            case BATCH:
                BatchContestData batchContestData = (BatchContestData) contestData;
                this.batchId = batchContestData.getBatchId();
                break;
            default:
                throw PortableException.of("A-08-001", contest.getAccessType());
        }
        this.problemLock = problemLock;
        this.problemRealId = contestData.getProblemList().stream()
                .map(BaseContestData.ContestProblemData::getProblemId)
                .collect(Collectors.toList());
    }

    public static ContestAdminDetailResponse of(Contest contest,
                                                BaseContestData contestData,
                                                String ownerHandle,
                                                List<ProblemListResponse> problemList,
                                                Set<String> coAuthor,
                                                List<Boolean> problemLock,
                                                Set<String> inviteUserSet) throws PortableException {
        return new ContestAdminDetailResponse(contest,
                contestData,
                ownerHandle,
                problemList,
                coAuthor,
                problemLock,
                inviteUserSet);
    }
}
