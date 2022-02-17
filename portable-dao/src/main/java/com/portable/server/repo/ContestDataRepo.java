package com.portable.server.repo;

import com.portable.server.model.contest.BasicContestData;
import com.portable.server.model.contest.PasswordContestData;
import com.portable.server.model.contest.PrivateContestData;
import com.portable.server.model.contest.PublicContestData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shiroha
 */
@Component
public class ContestDataRepo {

    private static final String COLLECTION_NAME = "contestData";

    @Resource
    private MongoTemplate mongoTemplate;

    public PublicContestData getPublicContestDataById(String datId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(datId)), PublicContestData.class, COLLECTION_NAME);
    }

    public PasswordContestData getPasswordContestDataById(String datId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(datId)), PasswordContestData.class, COLLECTION_NAME);
    }

    public PrivateContestData getPrivateContestDataById(String datId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(datId)), PrivateContestData.class, COLLECTION_NAME);
    }

    public void insertContestData(BasicContestData contestData) {
        mongoTemplate.insert(contestData, COLLECTION_NAME);
    }

    public void saveContestData(BasicContestData contestData) {
        mongoTemplate.save(contestData, COLLECTION_NAME);
    }
}
