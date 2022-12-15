package com.portable.server.manager.impl.prod;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ContestManager;
import com.portable.server.mapper.ContestRepo;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.repo.ContestDataRepo;
import com.portable.server.type.ContestAccessType;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
public class ContestManagerImpl implements ContestManager {

    @Resource
    private ContestRepo contestRepo;

    @Resource
    private ContestDataRepo contestDataRepo;

    @Override
    public @NotNull Integer getAllContestNumber() {
        return contestRepo.getAllContestNumber();
    }

    @Override
    public @NotNull List<Contest> getContestByPage(Integer pageSize, Integer offset) {
        return contestRepo.getContestByPage(pageSize, offset);
    }

    @Override
    public Optional<Contest> getContestById(Long id) {
        return Optional.ofNullable(contestRepo.getContestById(id));
    }

    @Override
    public void insertContest(Contest contest) {
        contestRepo.insertContest(contest);
    }

    @Override
    public void updateOwner(Long id, Long newOwner) {
        contestRepo.updateOwner(id, newOwner);
    }

    @Override
    public void updateStartTime(Long id, Date newStartTime) {
        contestRepo.updateStartTime(id, newStartTime);
    }

    @Override
    public void updateDuration(Long id, Integer newDuration) {
        contestRepo.updateDuration(id, newDuration);
    }

    @Override
    public void updateAccessType(Long id, ContestAccessType newAccessType) {
        contestRepo.updateAccessType(id, newAccessType);
    }

    @Override
    public void updateTitle(Long id, String title) {
        contestRepo.updateTitle(id, title);
    }

    @Override
    public BaseContestData getBaseContestDataById(String datId, ContestAccessType accessType) {
        switch (accessType) {
            case PUBLIC:
                return getPublicContestDataById(datId);
            case PASSWORD:
                return getPasswordContestDataById(datId);
            case PRIVATE:
                return getPrivateContestDataById(datId);
            case BATCH:
                return getBatchContestDataById(datId);
            default:
                throw PortableException.of("A-08-001", accessType);
        }
    }

    @Override
    public PublicContestData getPublicContestDataById(String datId) {
        return Optional.ofNullable(contestDataRepo.getPublicContestDataById(datId)).orElseThrow(PortableException.from("S-07-002"));
    }

    @Override
    public PasswordContestData getPasswordContestDataById(String datId) {
        return Optional.ofNullable(contestDataRepo.getPasswordContestDataById(datId)).orElseThrow(PortableException.from("S-07-002"));
    }

    @Override
    public PrivateContestData getPrivateContestDataById(String datId) {
        return Optional.ofNullable(contestDataRepo.getPrivateContestDataById(datId)).orElseThrow(PortableException.from("S-07-002"));
    }

    @Override
    public BatchContestData getBatchContestDataById(String datId) {
        return Optional.ofNullable(contestDataRepo.getBatchContestDataById(datId)).orElseThrow(PortableException.from("S-07-002"));
    }

    @Override
    public void insertContestData(BaseContestData contestData) {
        contestDataRepo.insertContestData(contestData);
    }

    @Override
    public void saveContestData(BaseContestData contestData) {
        contestDataRepo.saveContestData(contestData);
    }
}
