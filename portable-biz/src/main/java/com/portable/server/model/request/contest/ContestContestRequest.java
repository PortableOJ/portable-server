package com.portable.server.model.request.contest;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.BasicContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.type.ContestAccessType;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shiroha
 */
@Data
public class ContestContestRequest {

    /**
     * 数据库主键
     */
    private Long id;

    /**
     * 比赛标题
     */
    private String title;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 持续时间（分钟）
     */
    private Integer duration;

    /**
     * 访问权限
     */
    private ContestAccessType accessType;

    /**
     * 访问权限的配置密码
     */
    private String password;

    /**
     * 访问权限的配置邀请用户
     */
    private Set<String> inviteUserSet;

    /**
     * 题目列表
     */
    private List<Long> problemList;

    /**
     * 共同的出题人的昵称
     */
    private Set<String> coAuthor;

    /**
     * 封榜时长
     */
    private Integer freezeTime;

    /**
     * 公告
     */
    private String announcement;

    /**
     * 惩罚时间（分钟）
     */
    private Integer penaltyTime;

    public void toContest(Contest contest) {
        contest.setId(this.id);
        contest.setTitle(this.title);
        contest.setStartTime(this.startTime);
        contest.setDuration(this.duration);
        contest.setAccessType(this.accessType);
    }

    public void toContestData(BasicContestData contestData, Set<Long> coAuthorIdSet, Set<Long> inviteUserIdSet) throws PortableException {
        contestData.setCoAuthor(coAuthorIdSet);
        contestData.setFreezeTime(this.freezeTime);
        contestData.setAnnouncement(this.announcement);
        contestData.setPenaltyTime(this.penaltyTime);
        Map<Long, BasicContestData.ContestProblemData> problemDataMap = contestData.getProblemList()
                .stream()
                .collect(Collectors.toMap(BasicContestData.ContestProblemData::getProblemId, contestProblemData -> contestProblemData));
        List<BasicContestData.ContestProblemData> newProblemList = problemList.stream()
                .parallel()
                .map(aLong -> {
                    if (problemDataMap.containsKey(aLong)) {
                        return problemDataMap.get(aLong);
                    } else {
                        return new BasicContestData.ContestProblemData(aLong);
                    }
                })
                .collect(Collectors.toList());
        contestData.setProblemList(newProblemList);
        switch (this.accessType) {
            case PUBLIC:
                break;
            case PASSWORD:
                PasswordContestData passwordContestData = (PasswordContestData) contestData;
                passwordContestData.setPassword(this.password);
                break;
            case PRIVATE:
                if (this.inviteUserSet == null) {
                    throw PortableException.of("A-08-009");
                }
                PrivateContestData privateContestData = (PrivateContestData) contestData;
                privateContestData.setInviteUserSet(inviteUserIdSet);
                break;
            default:
                throw PortableException.of("A-08-001", this.accessType);
        }
    }
}
