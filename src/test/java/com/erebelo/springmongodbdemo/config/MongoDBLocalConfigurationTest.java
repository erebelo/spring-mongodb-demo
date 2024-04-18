package com.erebelo.springmongodbdemo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MongoDBLocalConfigurationTest {

    @InjectMocks
    private MongoDBLocalConfiguration mongoDBLocalConfiguration;

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "27017";
    private static final String DB_NAME = "demo-db";
    private static final String LOCAL_CONNECTION_STRING = "mongodb://localhost:27017/demo-db?replicaSet=rs0";

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(mongoDBLocalConfiguration, "dbHost", DB_HOST);
        ReflectionTestUtils.setField(mongoDBLocalConfiguration, "dbPort", DB_PORT);
        ReflectionTestUtils.setField(mongoDBLocalConfiguration, "dbName", DB_NAME);
    }

    @Test
    void testConfigureClientSettings() {
        MongoClientSettings.Builder builderMock = Mockito.mock(MongoClientSettings.Builder.class);

        given(builderMock.applyConnectionString(any(ConnectionString.class))).willAnswer(invocation -> {
            MongoClientSettings.Builder newBuilder = Mockito.mock(MongoClientSettings.Builder.class);
            given(newBuilder.retryReads(Boolean.FALSE)).willReturn(newBuilder);
            return newBuilder;
        });

        mongoDBLocalConfiguration.configureClientSettings(builderMock);

        verify(builderMock).applyConnectionString(new ConnectionString(LOCAL_CONNECTION_STRING));
    }
}
