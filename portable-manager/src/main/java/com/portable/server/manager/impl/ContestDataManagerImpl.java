package com.portable.server.manager.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ContestDataManager;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import com.portable.server.repo.ContestDataRepo;
import com.portable.server.type.ContestAccessType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;

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
            default:
                throw PortableException.of("A-08-001", accessType);
        }
    }

    @Override
    public PublicContestData getPublicContestDataById(String datId) {
        return contestDataRepo.getPublicContestDataById(datId);
    }

    @Override
    public PasswordContestData getPasswordContestDataById(String datId) {
        return contestDataRepo.getPasswordContestDataById(datId);
    }

    @Override
    public PrivateContestData getPrivateContestDataById(String datId) {
        return contestDataRepo.getPrivateContestDataById(datId);
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
