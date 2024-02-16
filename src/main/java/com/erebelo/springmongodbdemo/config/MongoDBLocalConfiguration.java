package com.erebelo.springmongodbdemo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class MongoDBLocalConfiguration extends MongoDBConfiguration {

    private static final String LOCAL_CONNECTION_STRING_TEMPLATE = "mongodb://%s:%s/%s?replicaSet=rs0&authSource=admin";

    public MongoDBLocalConfiguration(Environment env) {
        super(env);
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(getLocalConnectionString())
                .retryReads(Boolean.FALSE)
                .retryWrites(Boolean.FALSE);
    }

    private ConnectionString getLocalConnectionString() {
        return new ConnectionString(String.format(LOCAL_CONNECTION_STRING_TEMPLATE, clusterURL, clusterPort,
                getDatabaseName()));
    }
}
