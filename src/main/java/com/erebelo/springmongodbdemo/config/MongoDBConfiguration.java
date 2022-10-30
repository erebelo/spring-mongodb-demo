package com.erebelo.springmongodbdemo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
@Profile("!local")
public class MongoDBConfiguration extends AbstractMongoClientConfiguration {

    private static final String CONNECTION_STRING_TEMPLATE = "mongodb://%s%s/%s?replicaSet=rs0&authSource=admin";

    @Autowired
    private Environment environment;

    @Value("${database.clusterURL:localhost}")
    protected String clusterURL;

    @Value("${database.clusterPort:27017}")
    protected String clusterPort;

    @Value("${database.dbName:resolver-demo-db}")
    protected String dbName;

    @Value("${database.dbUsername:}")
    protected String dbUsername;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    // Heap memory security breach: do not use @Value annotation to get passwords
    private String getDbPassword() {
        return environment.getProperty("database.password");
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(getConnectionString())
                .retryReads(Boolean.FALSE)
                .retryWrites(Boolean.FALSE);
    }

    private ConnectionString getConnectionString() {
        var databaseCredentials = String.format("%s:%s@", dbUsername, getDbPassword());
        var clusterEndpoint = String.format("%s:%s", clusterURL, clusterPort);

        return new ConnectionString(String.format(CONNECTION_STRING_TEMPLATE, databaseCredentials, clusterEndpoint,
                getDatabaseName()));
    }
}
