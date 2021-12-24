package com.portable.server.repo;

import com.portable.server.model.solution.SolutionData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shiroha
 */
@Component
public class SolutionDataRepo {

    private static final String COLLECTION_NAME = "solutionData";

    @Resource
    private MongoTemplate mongoTemplate;

    public SolutionData getSolutionData(String dataId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(dataId)), SolutionData.class, COLLECTION_NAME);
    }

    public void insertSolutionData(SolutionData solutionData) {
        mongoTemplate.insert(solutionData, COLLECTION_NAME);
    }

    public void saveSolutionData(SolutionData solutionData) {
        mongoTemplate.save(solutionData);
    }
}
