package com.xuecheng.manage_cms_client.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/5 15:07
 */
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    String db;
    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoclient){
        MongoDatabase database = mongoclient.getDatabase(db);
        GridFSBucket bucket = GridFSBuckets.create(database);
        return bucket;
    }
}