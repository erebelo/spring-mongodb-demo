package com.erebelo.springmongodbdemo.config;

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
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Objects;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
@Profile("!local")
@RequiredArgsConstructor
public class MongoDBConfiguration extends AbstractMongoClientConfiguration {

    private static final String CONNECTION_STRING_TEMPLATE = "mongodb://%s:%s@%s:%s/%s?ssl=true&replicaSet=rs0"
            + "&authSource=admin";

    private final Environment env;

    @Value("${database.host:localhost}")
    protected String dbHost;

    @Value("${database.port:27017}")
    protected String dbPort;

    @Value("${database.name:demo_db}")
    private String dbName;

    @Value("${database.username:}")
    private String dbUsername;

    @Value("${database.ssl.keystore}")
    private String keystore;

    @Value("${database.ssl.keystore.password}")
    private String keystorePassword;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    // Heap memory security breach: do not use @Value or @ConfigurationProperties
    // annotation to get passwords
    private String getDbPassword() {
        return env.getProperty("database.password");
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        // Converts before persisting the document in the database and after fetching it

        // LocalDate
        adapter.registerConverter(new LocalDateWritingConverter());
        adapter.registerConverter(new LocalDateReadingConverter());

        // EnumValueType
        adapter.registerConverter(new EnumValueTypeWritingConverter());
        adapter.registerConverterFactory(new EnumValueTypeReadingConverter());

        // EnumIdValueType
        adapter.registerConverter(new EnumIdValueTypeWritingConverter());
        adapter.registerConverterFactory(new EnumIdValueTypeReadingConverter());

        // EnumCodeValueType
        adapter.registerConverter(new EnumCodeValueTypeWritingConverter());
        adapter.registerConverterFactory(new EnumCodeValueTypeReadingConverter());
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(getConnectionString()).retryReads(Boolean.FALSE).retryWrites(Boolean.FALSE)
                .applyToSslSettings(bdr -> {
                    bdr.enabled(Boolean.TRUE);
                    bdr.context(createSslContext());
                });
    }

    private ConnectionString getConnectionString() {
        return new ConnectionString(String.format(CONNECTION_STRING_TEMPLATE, dbUsername, getDbPassword(), dbHost,
                dbPort, getDatabaseName()));
    }

    private SSLContext createSslContext() {
        try {
            Objects.requireNonNull(keystore, "Keystore path cannot be null");
            Objects.requireNonNull(keystorePassword, "Keystore password cannot be null");

            final String KEYSTORE_TYPE = "PKCS12";
            final String SSL_PROTOCOL = "TLSv1.3";

            KeyStore keyStoreInstance = KeyStore.getInstance(KEYSTORE_TYPE);

            try (InputStream in = getClass().getClassLoader().getResourceAsStream(this.keystore)) {
                if (in == null) {
                    throw new IOException("Keystore file not found: " + this.keystore);
                }
                keyStoreInstance.load(in, keystorePassword.toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStoreInstance, keystorePassword.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStoreInstance);

            SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                    SecureRandom.getInstanceStrong());

            return sslContext;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load keystore", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("SSL context initialization failed", e);
        }
    }
}
