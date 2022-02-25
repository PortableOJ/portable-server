package com.portable.server.model.response.contest;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.BasicContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
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
     * 访问权限的配置密码
     */
    private String password;

    /**
     * 访问权限的配置邀请用户
     */
    private Set<String> inviteUserSet;

    /**
     * 题目是否是锁定的
     */
    private List<Boolean> problemLock;

    ContestAdminDetailResponse(Contest contest,
                               BasicContestData contestData,
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
            default:
                throw PortableException.of("A-08-001", contest.getAccessType());
        }
        this.problemLock = problemLock;
    }

    public static ContestAdminDetailResponse of(Contest contest,
                                                BasicContestData contestData,
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
