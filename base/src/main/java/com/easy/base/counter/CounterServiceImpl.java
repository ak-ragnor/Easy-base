package com.easy.base.counter;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class CounterServiceImpl implements CounterService{
    private final MongoOperations mongoOperations;

    public CounterServiceImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public long getGeneratedCounter(String seq) {
        Counter counter = mongoOperations.findAndModify(query(where("_id").is(seq)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                Counter.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
