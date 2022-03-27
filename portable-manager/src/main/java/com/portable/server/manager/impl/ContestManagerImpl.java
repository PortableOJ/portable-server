package com.portable.server.manager.impl;

import com.portable.server.manager.ContestManager;
import com.portable.server.mapper.ContestMapper;
import com.portable.server.model.contest.Contest;
import com.portable.server.type.ContestAccessType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author shiroha
 */
@Component
public class ContestManagerImpl implements ContestManager {

    @Resource
    private ContestMapper contestMapper;

    @Override
    public @NotNull Contest insertContest() {
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
    public @NotNull Integer getAllContestNumber() {
        return contestMapper.getAllContestNumber();
    }

    @Override
    public @NotNull List<Contest> getContestByPage(Integer pageSize, Integer offset) {
        return contestMapper.getContestByPage(pageSize, offset);
    }

    @Override
    public Optional<Contest> getContestById(Long id) {
        return Optional.ofNullable(contestMapper.getContestById(id));
    }

    @Override
    public void insertContest(Contest contest) {
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
