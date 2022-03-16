package com.portable.server.repo;

import com.portable.server.model.user.BaseUserData;
import com.portable.server.model.user.BatchUserData;
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
public class UserDataRepo {

    private static final String COLLECTION_NAME = "userData";

    @Resource
    private MongoTemplate mongoTemplate;

    public NormalUserData getNormalUserDataById(String dataId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(dataId)), NormalUserData.class, COLLECTION_NAME);
    }

    public BatchUserData getBatchUserDataById(String dataId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(dataId)), BatchUserData.class, COLLECTION_NAME);
    }

    public void insertUserData(BaseUserData baseUserData) {
        mongoTemplate.insert(baseUserData, COLLECTION_NAME);
    }

    public void saveUserData(BaseUserData baseUserData) {
        mongoTemplate.save(baseUserData, COLLECTION_NAME);
    }
}
