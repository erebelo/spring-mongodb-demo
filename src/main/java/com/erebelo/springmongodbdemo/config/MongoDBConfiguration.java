package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.converter.DocumentToEnumTypeIdConverterFactory;
import com.erebelo.springmongodbdemo.context.converter.EnumTypeIdToDocumentConverter;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
@Profile("!local")
public class MongoDBConfiguration extends AbstractMongoClientConfiguration {

    private static final String CONNECTION_STRING_TEMPLATE = "mongodb://%s%s/%s?replicaSet=rs0&authSource=admin";

    @Autowired
    private Environment env;

    @Value("${database.clusterURL:localhost}")
    protected String clusterURL;

    @Value("${database.clusterPort:27017}")
    protected String clusterPort;

    @Value("${database.dbName:demo-db}")
    protected String dbName;

    @Value("${database.dbUsername:}")
    protected String dbUsername;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    // Heap memory security breach: do not use @Value annotation to get passwords
    private String getDbPassword() {
        return env.getProperty("database.password");
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        // Enable the mongodb to convert the enum type to a document before persisting it and the document to an enum type after fetching the data
        adapter.registerConverter(EnumTypeIdToDocumentConverter.INSTANCE);
        adapter.registerConverterFactory(DocumentToEnumTypeIdConverterFactory.INSTANCE);
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
