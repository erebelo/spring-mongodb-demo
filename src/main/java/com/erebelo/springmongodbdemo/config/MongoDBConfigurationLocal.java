package com.erebelo.springmongodbdemo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class MongoDBConfigurationLocal extends MongoDBConfiguration {

    private static final String LOCAL_CONNECTION_STRING_TEMPLATE = "mongodb://%s:%s/%s?replicaSet=rs0&authSource=admin";

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(getConnectionString())
                .retryReads(Boolean.FALSE)
                .retryWrites(Boolean.FALSE);
    }

    private ConnectionString getConnectionString() {
        return new ConnectionString(String.format(LOCAL_CONNECTION_STRING_TEMPLATE, clusterURL, clusterPort,
                getDatabaseName()));
    }
}
