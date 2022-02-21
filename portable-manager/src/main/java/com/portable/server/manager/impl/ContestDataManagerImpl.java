package com.portable.server.manager.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.ContestDataManager;
import com.portable.server.model.contest.BasicContestData;
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
    public BasicContestData newContestData(ContestAccessType accessType) throws PortableException {
        switch (accessType) {
            case PUBLIC:
                return PublicContestData.builder()
                        .problemList(new ArrayList<>())
                        .coAuthor(new HashSet<>())
                        .freezeTime(0L)
                        .announcement("")
                        .build();
            case PASSWORD:
                return PasswordContestData.builder()
                        .problemList(new ArrayList<>())
                        .coAuthor(new HashSet<>())
                        .freezeTime(0L)
                        .announcement("")
                        .password("")
                        .build();
            case PRIVATE:
                return PrivateContestData.builder()
                        .problemList(new ArrayList<>())
                        .coAuthor(new HashSet<>())
                        .freezeTime(0L)
                        .announcement("")
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
    public void insertContestData(BasicContestData contestData) {
        contestDataRepo.insertContestData(contestData);
    }

    @Override
    public void saveContestData(BasicContestData contestData) {
        contestDataRepo.saveContestData(contestData);
    }
}
