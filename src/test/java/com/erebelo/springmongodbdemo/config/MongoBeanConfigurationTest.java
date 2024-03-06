package com.erebelo.springmongodbdemo.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MongoBeanConfigurationTest {

    @InjectMocks
    private MongoBeanConfiguration mongoBeanConfiguration;

    @Mock
    private MongoDatabaseFactory mongoDatabaseFactory;

    @Mock
    private LocalValidatorFactoryBean localValidatorFactoryBean;

    @Test
    void testTransactionManagerBean() {
        MongoTransactionManager transactionManager = mongoBeanConfiguration.transactionManager(mongoDatabaseFactory);

        assertNotNull(transactionManager);
    }

    @Test
    void testValidatingMongoEventListenerBean() {
        ValidatingMongoEventListener validatingMongoEventListener = mongoBeanConfiguration.validatingMongoEventListener(localValidatorFactoryBean);

        assertNotNull(validatingMongoEventListener);
    }
}
