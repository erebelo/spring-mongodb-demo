package com.erebelo.springmongodbdemo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MongoDBConfigurationTest {

    @InjectMocks
    private MongoDBConfiguration mongoDBConfiguration;

    @Mock
    private Environment environment;

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "27017";
    private static final String DB_NAME = "demo_db";
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "admin";
    private static final String CONNECTION_STRING = "mongodb://admin:admin@localhost:27017/demo_db?replicaSet=rs0";

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(mongoDBConfiguration, "dbHost", DB_HOST);
        ReflectionTestUtils.setField(mongoDBConfiguration, "dbPort", DB_PORT);
        ReflectionTestUtils.setField(mongoDBConfiguration, "dbName", DB_NAME);
        ReflectionTestUtils.setField(mongoDBConfiguration, "dbUsername", DB_USERNAME);
    }

    @Test
    void testGetDatabaseName() {
        var databaseName = mongoDBConfiguration.getDatabaseName();

        assertEquals(DB_NAME, databaseName);
    }

    @Test
    void testConfigureConverters() {
        MongoCustomConversions.MongoConverterConfigurationAdapter adapterMock = Mockito
                .mock(MongoCustomConversions.MongoConverterConfigurationAdapter.class);

        mongoDBConfiguration.configureConverters(adapterMock);

        verify(adapterMock, Mockito.times(5)).registerConverter(any());
    }

    @Test
    void testConfigureClientSettings() {
        MongoClientSettings.Builder builderMock = Mockito.mock(MongoClientSettings.Builder.class);

        given(builderMock.applyConnectionString(any(ConnectionString.class))).willAnswer(invocation -> {
            MongoClientSettings.Builder newBuilder = Mockito.mock(MongoClientSettings.Builder.class);
            given(newBuilder.retryReads(Boolean.FALSE)).willReturn(newBuilder);
            return newBuilder;
        });

        given(environment.getProperty("database.password")).willReturn(DB_PASSWORD);

        mongoDBConfiguration.configureClientSettings(builderMock);

        verify(builderMock).applyConnectionString(new ConnectionString(CONNECTION_STRING));
    }
}
