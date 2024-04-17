package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.converter.DocumentToEnumCodeValueTypeConverter;
import com.erebelo.springmongodbdemo.context.converter.DocumentToEnumIdTypeConverter;
import com.erebelo.springmongodbdemo.context.converter.DocumentToEnumTypeConverter;
import com.erebelo.springmongodbdemo.context.converter.EnumCodeValueTypeToDocumentConverter;
import com.erebelo.springmongodbdemo.context.converter.EnumIdTypeToDocumentConverter;
import com.erebelo.springmongodbdemo.context.converter.EnumTypeToDocumentConverter;
import com.erebelo.springmongodbdemo.context.converter.LocalDateToStringConverter;
import com.erebelo.springmongodbdemo.context.converter.StringToLocalDateConverter;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
@RequiredArgsConstructor
public class MongoDBConfiguration extends AbstractMongoClientConfiguration {

    private static final String CONNECTION_STRING_TEMPLATE = "mongodb://%s%s/%s?replicaSet=rs0";

    private final Environment env;

    @Value("${database.host:localhost}")
    protected String dbHost;

    @Value("${database.port:27017}")
    protected String dbPort;

    @Value("${database.name:demo-db}")
    protected String dbName;

    @Value("${database.username:}")
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
        // Converts before persisting the document in the database and after fetching it

        //LocalDate
        adapter.registerConverter(LocalDateToStringConverter.INSTANCE);
        adapter.registerConverter(StringToLocalDateConverter.INSTANCE);

        // EnumType
        adapter.registerConverter(EnumTypeToDocumentConverter.INSTANCE);
        adapter.registerConverterFactory(DocumentToEnumTypeConverter.INSTANCE);

        // EnumIdType
        adapter.registerConverter(EnumIdTypeToDocumentConverter.INSTANCE);
        adapter.registerConverterFactory(DocumentToEnumIdTypeConverter.INSTANCE);

        // EnumCodeValueType
        adapter.registerConverter(EnumCodeValueTypeToDocumentConverter.INSTANCE);
        adapter.registerConverterFactory(DocumentToEnumCodeValueTypeConverter.INSTANCE);
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(getConnectionString()).retryReads(Boolean.FALSE).retryWrites(Boolean.FALSE);
    }

    private ConnectionString getConnectionString() {
        var databaseCredentials = String.format("%s:%s@", dbUsername, getDbPassword());
        var clusterEndpoint = String.format("%s:%s", dbHost, dbPort);

        return new ConnectionString(String.format(CONNECTION_STRING_TEMPLATE, databaseCredentials, clusterEndpoint, getDatabaseName()));
    }
}
