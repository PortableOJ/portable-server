package com.portable.server.manager.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ContestDataManager;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.BatchContestData;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.repo.ContestDataRepo;
import com.portable.server.type.ContestAccessType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

/**
 * @author shiroha
 */
@Component
public class ContestDataManagerImpl implements ContestDataManager {

    @Resource
    private ContestDataRepo contestDataRepo;

    @Override
    public BaseContestData newContestData(ContestAccessType accessType) throws PortableException {
        switch (accessType) {
            case PUBLIC:
                return PublicContestData.builder()
                        .problemList(new ArrayList<>())
                        .coAuthor(new HashSet<>())
                        .freezeTime(0)
                        .announcement("")
                        .penaltyTime(0)
                        .build();
            case PASSWORD:
                return PasswordContestData.builder()
                        .problemList(new ArrayList<>())
                        .coAuthor(new HashSet<>())
                        .freezeTime(0)
                        .announcement("")
                        .penaltyTime(0)
                        .password("")
                        .build();
            case PRIVATE:
                return PrivateContestData.builder()
                        .problemList(new ArrayList<>())
                        .coAuthor(new HashSet<>())
                        .freezeTime(0)
                        .announcement("")
                        .penaltyTime(0)
                        .inviteUserSet(new HashSet<>())
                        .build();
            case BATCH:
                return BatchContestData.builder()
                        .problemList(new ArrayList<>())
                        .coAuthor(new HashSet<>())
                        .freezeTime(0)
                        .announcement("")
                        .penaltyTime(0)
                        .batchId(null)
                        .build();
            default:
                throw PortableException.of("A-08-001", accessType);
        }
    }

    @Override
    public BaseContestData getBaseContestDataById(String datId, ContestAccessType accessType) throws PortableException {
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
    public PublicContestData getPublicContestDataById(String datId) throws PortableException {
        return Optional.ofNullable(contestDataRepo.getPublicContestDataById(datId)).orElseThrow(PortableException.from("S-07-002"));
    }

    @Override
    public PasswordContestData getPasswordContestDataById(String datId) throws PortableException {
        return Optional.ofNullable(contestDataRepo.getPasswordContestDataById(datId)).orElseThrow(PortableException.from("S-07-002"));
    }

    @Override
    public PrivateContestData getPrivateContestDataById(String datId) throws PortableException {
        return Optional.ofNullable(contestDataRepo.getPrivateContestDataById(datId)).orElseThrow(PortableException.from("S-07-002"));
    }

    @Override
    public BatchContestData getBatchContestDataById(String datId) throws PortableException {
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
