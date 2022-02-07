package com.portable.server.repo;

import com.portable.server.model.user.NormalUserData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shiroha
 */
@Component
public class NormalUserDataRepo {

    private static final String COLLECTION_NAME = "normalUser";

    @Resource
    private MongoTemplate mongoTemplate;

    public NormalUserData getUserDataById(String dataId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(dataId)), NormalUserData.class, COLLECTION_NAME);
    }

    public void insertUserData(NormalUserData normalUserData) {
        mongoTemplate.insert(normalUserData, COLLECTION_NAME);
    }

    public void saveUserData(NormalUserData normalUserData) {
        mongoTemplate.save(normalUserData, COLLECTION_NAME);
    }
}
