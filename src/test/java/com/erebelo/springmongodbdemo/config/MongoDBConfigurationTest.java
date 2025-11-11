package com.erebelo.springmongodbdemo.config;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.context.converter.EnumCodeValueTypeReadingConverter;
import com.erebelo.springmongodbdemo.context.converter.EnumCodeValueTypeWritingConverter;
import com.erebelo.springmongodbdemo.context.converter.EnumIdValueTypeReadingConverter;
import com.erebelo.springmongodbdemo.context.converter.EnumIdValueTypeWritingConverter;
import com.erebelo.springmongodbdemo.context.converter.EnumValueTypeReadingConverter;
import com.erebelo.springmongodbdemo.context.converter.EnumValueTypeWritingConverter;
import com.erebelo.springmongodbdemo.context.converter.LocalDateReadingConverter;
import com.erebelo.springmongodbdemo.context.converter.LocalDateWritingConverter;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MongoDBConfigurationTest {

    @InjectMocks
    private MongoDBConfiguration config;

    @Mock
    private Environment environment;

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "27017";
    private static final String DB_NAME = "demo_db";
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "admin";
    private static final String DB_KEYSTORE = "mock-keystore.p12";
    private static final String DB_KEYSTORE_PASSWORD = "mockKeystorePassword";
    private static final String CONNECTION_STRING = "mongodb://admin:admin@localhost:27017/demo_db?ssl=true"
            + "&replicaSet=rs0&authSource=admin";
    private static final String KEYSTORE_PROPERTY = "keystore";
    private static final String KEYSTORE_PASSWORD_PROPERTY = "keystorePassword";

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(config, "dbHost", DB_HOST);
        ReflectionTestUtils.setField(config, "dbPort", DB_PORT);
        ReflectionTestUtils.setField(config, "dbName", DB_NAME);
        ReflectionTestUtils.setField(config, "dbUsername", DB_USERNAME);
    }

    @Test
    void testGetDatabaseName() {
        String databaseName = config.getDatabaseName();

        assertEquals(DB_NAME, databaseName);
    }

    @Test
    void testMongoCustomConversions() {
        MongoCustomConversions conversions = config.customConversions();

        assertNotNull(conversions);

        // Uses reflection to access getConverters()
        List<?> converterList = (List<?>) ReflectionTestUtils.getField(conversions, "converters");

        assertThat(converterList).anyMatch(LocalDateWritingConverter.class::isInstance)
                .anyMatch(LocalDateReadingConverter.class::isInstance)
                .anyMatch(EnumValueTypeWritingConverter.class::isInstance)
                .anyMatch(EnumValueTypeReadingConverter.class::isInstance)
                .anyMatch(EnumIdValueTypeWritingConverter.class::isInstance)
                .anyMatch(EnumIdValueTypeReadingConverter.class::isInstance)
                .anyMatch(EnumCodeValueTypeWritingConverter.class::isInstance)
                .anyMatch(EnumCodeValueTypeReadingConverter.class::isInstance);
    }

    @Test
    void testConfigureClientSettings() {
        MongoClientSettings.Builder builderMock = Mockito.mock(MongoClientSettings.Builder.class);

        given(builderMock.applyConnectionString(any(ConnectionString.class))).willAnswer(invocation -> {
            MongoClientSettings.Builder newBuilder = Mockito.mock(MongoClientSettings.Builder.class);
            given(newBuilder.retryReads(Boolean.FALSE)).willReturn(newBuilder);
            given(newBuilder.retryWrites(Boolean.FALSE)).willReturn(newBuilder);
            given(newBuilder.applyToSslSettings(any())).willReturn(newBuilder);
            return newBuilder;
        });

        given(environment.getProperty("database.password")).willReturn(DB_PASSWORD);

        config.configureClientSettings(builderMock);

        verify(builderMock).applyConnectionString(new ConnectionString(CONNECTION_STRING));
    }

    @Test
    void testConfigureClientSettingsWithSslContextSuccessful() throws Exception {
        ReflectionTestUtils.setField(config, KEYSTORE_PROPERTY, DB_KEYSTORE);
        ReflectionTestUtils.setField(config, KEYSTORE_PASSWORD_PROPERTY, DB_KEYSTORE_PASSWORD);

        try (MockedStatic<KeyStore> keyStoreMockedStatic = mockStatic(KeyStore.class);
                MockedStatic<KeyManagerFactory> keyManagerFactoryMockedStatic = mockStatic(KeyManagerFactory.class);
                MockedStatic<TrustManagerFactory> trustManagerFactoryMockedStatic = mockStatic(
                        TrustManagerFactory.class)) {

            KeyStore mockKeyStore = mock(KeyStore.class);
            keyStoreMockedStatic.when(() -> KeyStore.getInstance("PKCS12")).thenReturn(mockKeyStore);
            willDoNothing().given(mockKeyStore).load(any(), any());

            KeyManagerFactory mockKeyManagerFactory = mock(KeyManagerFactory.class);
            keyManagerFactoryMockedStatic
                    .when(() -> KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()))
                    .thenReturn(mockKeyManagerFactory);
            willDoNothing().given(mockKeyManagerFactory).init(mockKeyStore, DB_KEYSTORE_PASSWORD.toCharArray());

            TrustManagerFactory mockTrustManagerFactory = mock(TrustManagerFactory.class);
            trustManagerFactoryMockedStatic
                    .when(() -> TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()))
                    .thenReturn(mockTrustManagerFactory);
            willDoNothing().given(mockTrustManagerFactory).init(mockKeyStore);

            MongoClientSettings.Builder builder = MongoClientSettings.builder();
            config.configureClientSettings(builder);
            MongoClientSettings settings = builder.build();

            assertTrue(settings.getSslSettings().isEnabled());
            SSLContext sslContext = settings.getSslSettings().getContext();
            assertNotNull(sslContext, "SSLContext should not be null");
            assertEquals("TLSv1.3", sslContext.getProtocol(), "Protocol should be TLSv1.3");
        }
    }

    @Test
    void testConfigureClientSettingsWithSslContextThrowsNullPointerExceptionForMissingKeystore() {
        ReflectionTestUtils.setField(config, KEYSTORE_PROPERTY, null);

        MongoClientSettings.Builder builder = MongoClientSettings.builder();

        Exception exception = assertThrows(NullPointerException.class, () -> config.configureClientSettings(builder));
        assertTrue(exception.getMessage().contains("Keystore path cannot be null"));
    }

    @Test
    void testConfigureClientSettingsWithSslContextThrowsNullPointerExceptionForMissingKeystorePassword() {
        ReflectionTestUtils.setField(config, KEYSTORE_PROPERTY, DB_KEYSTORE);
        ReflectionTestUtils.setField(config, KEYSTORE_PASSWORD_PROPERTY, null);

        MongoClientSettings.Builder builder = MongoClientSettings.builder();

        Exception exception = assertThrows(NullPointerException.class, () -> config.configureClientSettings(builder));
        assertTrue(exception.getMessage().contains("Keystore password cannot be null"));
    }

    @Test
    void testConfigureClientSettingsWithSslContextThrowsIllegalStateExceptionForKeystoreNotFound() {
        ReflectionTestUtils.setField(config, KEYSTORE_PROPERTY, "nonexistent-keystore.p12");
        ReflectionTestUtils.setField(config, KEYSTORE_PASSWORD_PROPERTY, DB_KEYSTORE_PASSWORD);

        MongoClientSettings.Builder builder = MongoClientSettings.builder();

        Exception exception = assertThrows(IllegalStateException.class, () -> config.configureClientSettings(builder));
        assertTrue(exception.getMessage().contains("Failed to load keystore"));
    }

    @Test
    void testConfigureClientSettingsWithSslContextThrowsIllegalStateExceptionForGeneralSecurityException()
            throws Exception {
        ReflectionTestUtils.setField(config, KEYSTORE_PROPERTY, DB_KEYSTORE);
        ReflectionTestUtils.setField(config, KEYSTORE_PASSWORD_PROPERTY, DB_KEYSTORE_PASSWORD);

        try (MockedStatic<KeyStore> keyStoreMockedStatic = mockStatic(KeyStore.class)) {
            KeyStore mockKeyStore = mock(KeyStore.class);
            keyStoreMockedStatic.when(() -> KeyStore.getInstance("PKCS12")).thenReturn(mockKeyStore);
            willThrow(new CertificateException("CertificateException Exception")).given(mockKeyStore).load(any(),
                    any());

            MongoClientSettings.Builder builder = MongoClientSettings.builder();
            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> config.configureClientSettings(builder));

            assertEquals("SSL context initialization failed", thrown.getMessage());
            assertNotNull(thrown.getCause());
            assertTrue(thrown.getCause() instanceof GeneralSecurityException);
        }
    }
}
