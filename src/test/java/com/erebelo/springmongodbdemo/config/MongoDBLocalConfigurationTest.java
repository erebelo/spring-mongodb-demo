package com.erebelo.springmongodbdemo.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MongoDBLocalConfigurationTest {

    @InjectMocks
    private MongoDBLocalConfiguration config;

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "27017";
    private static final String DB_NAME = "demo_db";
    private static final String LOCAL_CONNECTION_STRING = "mongodb://localhost:27017/demo_db?ssl=false&replicaSet=rs0&authSource=admin";

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(config, "dbHost", DB_HOST);
        ReflectionTestUtils.setField(config, "dbPort", DB_PORT);
        ReflectionTestUtils.setField(config, "dbName", DB_NAME);
    }

    @Test
    void testConfigureClientSettings() {
        MongoClientSettings.Builder builderMock = Mockito.mock(MongoClientSettings.Builder.class);

        given(builderMock.applyConnectionString(any(ConnectionString.class))).willAnswer(invocation -> {
            MongoClientSettings.Builder newBuilder = Mockito.mock(MongoClientSettings.Builder.class);
            given(newBuilder.retryReads(Boolean.FALSE)).willReturn(newBuilder);
            given(newBuilder.retryWrites(Boolean.FALSE)).willReturn(newBuilder);
            return newBuilder;
        });

        config.configureClientSettings(builderMock);

        verify(builderMock).applyConnectionString(new ConnectionString(LOCAL_CONNECTION_STRING));
    }
}
