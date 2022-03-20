package com.portable.server.model.response.contest;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.response.problem.ProblemListResponse;
import com.portable.server.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Data
@NoArgsConstructor
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
                               User owner,
                               List<ProblemListResponse> problemList,
                               Set<User> coAuthor,
                               List<Boolean> problemLock,
                               Set<User> inviteUserSet) throws PortableException {
        super(contest, contestData, owner, problemList, coAuthor);
        switch (contest.getAccessType()) {
            case PUBLIC:
                break;
            case PASSWORD:
                PasswordContestData passwordContestData = (PasswordContestData) contestData;
                this.password = passwordContestData.getPassword();
                break;
            case PRIVATE:
                this.inviteUserSet = inviteUserSet.stream()
                        .parallel()
                        .filter(user -> !Objects.isNull(user))
                        .map(User::getHandle)
                        .collect(Collectors.toSet());
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
                                                User owner,
                                                List<ProblemListResponse> problemList,
                                                Set<User> coAuthor,
                                                List<Boolean> problemLock,
                                                Set<User> inviteUserSet) throws PortableException {
        return new ContestAdminDetailResponse(contest,
                contestData,
                owner,
                problemList,
                coAuthor,
                problemLock,
                inviteUserSet);
    }
}
