package com.portable.server.manager.impl;

import com.portable.server.manager.ContestManager;
import com.portable.server.mapper.ContestMapper;
import com.portable.server.model.contest.Contest;
import com.portable.server.type.ContestAccessType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author shiroha
 */
@Component
public class ContestManagerImpl implements ContestManager {

    @Resource
    private ContestMapper contestMapper;

    @Override
    public Contest newContest() {
        return Contest.builder()
                .id(null)
                .dataId(null)
                .owner(null)
                .title(null)
                .startTime(null)
                .duration(null)
                .accessType(ContestAccessType.PRIVATE)
                .build();
    }

    @Override
    public Integer getAllContestNumber() {
        return contestMapper.getAllContestNumber();
    }

    @Override
    public List<Contest> getContestByPage(Integer pageNum, Integer offset) {
        return contestMapper.getContestByPage(pageNum, offset);
    }

    @Override
    public Contest getContestById(Long id) {
        return contestMapper.getContestById(id);
    }

    @Override
    public void newContest(Contest contest) {
        contestMapper.newContest(contest);
    }

    @Override
    public void updateOwner(Long id, Long newOwner) {
        contestMapper.updateOwner(id, newOwner);
    }

    @Override
    public void updateStartTime(Long id, Date newStartTime) {
        contestMapper.updateStartTime(id, newStartTime);
    }

    @Override
    public void updateDuration(Long id, Integer newDuration) {
        contestMapper.updateDuration(id, newDuration);
    }

    @Override
    public void updateAccessType(Long id, ContestAccessType newAccessType) {
        contestMapper.updateAccessType(id, newAccessType);
    }
}
