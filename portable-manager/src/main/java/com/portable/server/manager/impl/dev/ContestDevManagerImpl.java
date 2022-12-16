package com.portable.server.manager.impl.dev;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ContestManager;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.persistent.StructuredHelper;
import com.portable.server.type.ContestAccessType;
import com.portable.server.util.BasicTranslateUtils;

import org.jetbrains.annotations.NotNull;

/**
 * @author shiroha
 */
public class ContestDevManagerImpl implements ContestManager {

    @Resource(name = "contestDevMapper")
    private StructuredHelper<Long, Contest> contestDevMapper;

    @Resource(name = "contestDataDevMapper")
    private StructuredHelper<String, BaseContestData> contestDataDevMapper;

    @Override
    public @NotNull Integer countAllContest() {
        return contestDevMapper.countAll();
    }

    @Override
    public @NotNull List<Contest> getContestByPage(Integer pageSize, Integer offset) {
        return contestDevMapper.searchListByPage(contest -> true, pageSize, offset, (o1, o2) -> Objects.compare(o1.getId(), o2.getId(), Long::compare));
    }

    @Override
    public Optional<Contest> getContestById(Long id) {
        return contestDevMapper.getDataById(id);
    }

    @Override
    public void insertContest(Contest contest) {
        contestDevMapper.insert(contest, BasicTranslateUtils::reLong);
    }

    @Override
    public void updateOwner(Long id, Long newOwner) {
        contestDevMapper.updateByFunction(id, contest -> contest.setOwner(newOwner));
    }

    @Override
    public void updateStartTime(Long id, Date newStartTime) {
        contestDevMapper.updateByFunction(id, contest -> contest.setStartTime(newStartTime));
    }

    @Override
    public void updateDuration(Long id, Integer newDuration) {
        contestDevMapper.updateByFunction(id, contest -> contest.setDuration(newDuration));
    }

    @Override
    public void updateAccessType(Long id, ContestAccessType newAccessType) {
        contestDevMapper.updateByFunction(id, contest -> contest.setAccessType(newAccessType));
    }

    @Override
    public void updateTitle(Long id, String title) {
        contestDevMapper.updateByFunction(id, contest -> contest.setTitle(title));
    }

    @Override
    public PublicContestData getPublicContestDataById(String datId) {
        BaseContestData contestData = contestDataDevMapper.getDataById(datId).orElseThrow(PortableException.from("S-07-002"));
        if (contestData instanceof PublicContestData) {
            return (PublicContestData) contestData;
        }
        throw PortableException.of("S-07-002");
    }

    @Override
    public PasswordContestData getPasswordContestDataById(String datId) {
        BaseContestData contestData = contestDataDevMapper.getDataById(datId).orElseThrow(PortableException.from("S-07-002"));
        if (contestData instanceof PasswordContestData) {
            return (PasswordContestData) contestData;
        }
        throw PortableException.of("S-07-002");
    }

    @Override
    public PrivateContestData getPrivateContestDataById(String datId) {
        BaseContestData contestData = contestDataDevMapper.getDataById(datId).orElseThrow(PortableException.from("S-07-002"));
        if (contestData instanceof PrivateContestData) {
            return (PrivateContestData) contestData;
        }
        throw PortableException.of("S-07-002");
    }

    @Override
    public BatchContestData getBatchContestDataById(String datId) {
        BaseContestData contestData = contestDataDevMapper.getDataById(datId).orElseThrow(PortableException.from("S-07-002"));
        if (contestData instanceof BatchContestData) {
            return (BatchContestData) contestData;
        }
        throw PortableException.of("S-07-002");
    }

    @Override
    public void insertContestData(BaseContestData contestData) {
        contestDataDevMapper.insert(contestData, BasicTranslateUtils::reString);
    }

    @Override
    public void saveContestData(BaseContestData contestData) {
        contestDataDevMapper.updateById(contestData);
    }
}
