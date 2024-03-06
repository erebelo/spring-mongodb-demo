package com.erebelo.springmongodbdemo.config;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MongoDBConfigurationTest {

    @InjectMocks
    private MongoDBConfiguration mongoDBConfiguration;

    @Mock
    private Environment environment;

    private static final String DATABASE_NAME = "demo-db";
    private static final String DATABASE_USERNAME = "test";
    private static final String DATABASE_PASSWORD = "password123";
    private static final String CLUSTER_URL = "localhost";
    private static final String CLUSTER_PORT = "27017";
    private static final String CONNECTION_STRING = "mongodb://test:password123@localhost:27017/demo-db?replicaSet=rs0&authSource=admin";

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(mongoDBConfiguration, "dbName", DATABASE_NAME);
        ReflectionTestUtils.setField(mongoDBConfiguration, "dbUsername", DATABASE_USERNAME);
        ReflectionTestUtils.setField(mongoDBConfiguration, "clusterURL", CLUSTER_URL);
        ReflectionTestUtils.setField(mongoDBConfiguration, "clusterPort", CLUSTER_PORT);
    }

    @Test
    void testGetDatabaseName() {
        var databaseName = mongoDBConfiguration.getDatabaseName();

        assertEquals(DATABASE_NAME, databaseName);
    }

    @Test
    void testConfigureConverters() {
        MongoCustomConversions.MongoConverterConfigurationAdapter adapterMock =
                Mockito.mock(MongoCustomConversions.MongoConverterConfigurationAdapter.class);

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

        given(environment.getProperty("database.password")).willReturn(DATABASE_PASSWORD);

        mongoDBConfiguration.configureClientSettings(builderMock);

        verify(builderMock).applyConnectionString(new ConnectionString(CONNECTION_STRING));
    }
}
