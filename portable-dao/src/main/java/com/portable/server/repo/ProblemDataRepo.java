package com.portable.server.repo;

import com.portable.server.model.problem.ProblemData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shiroha
 */
@Component
public class ProblemDataRepo {

    private static final String COLLECTION_NAME = "problemName";

    @Resource
    private MongoTemplate mongoTemplate;

    public ProblemData getProblemData(String dataId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(dataId)), ProblemData.class, COLLECTION_NAME);
    }

    public void insertProblemData(ProblemData problemData) {
        mongoTemplate.insert(problemData, COLLECTION_NAME);
    }

    public void saveProblemData(ProblemData problemData) {
        mongoTemplate.save(problemData, COLLECTION_NAME);
    }
}
