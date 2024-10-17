package com.erebelo.springmongodbdemo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.erebelo.springmongodbdemo.util.AuthenticationUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class MongoBeanConfigurationTest {

    private final MongoBeanConfiguration mongoBeanConfiguration = new MongoBeanConfiguration();

    private static final String USER_ID = "12345";

    @Test
    void testTransactionManagerBean() {
        MongoTransactionManager transactionManager = mongoBeanConfiguration
                .transactionManager(mock(MongoDatabaseFactory.class));

        assertNotNull(transactionManager);
    }

    @Test
    void testValidatingMongoEventListenerBean() {
        ValidatingMongoEventListener validatingMongoEventListener = mongoBeanConfiguration
                .validatingMongoEventListener(mock(LocalValidatorFactoryBean.class));

        assertNotNull(validatingMongoEventListener);
    }

    @Test
    void testAuditorProviderBean() {
        try (MockedStatic<AuthenticationUtil> mockedStatic = mockStatic(AuthenticationUtil.class)) {
            mockedStatic.when(AuthenticationUtil::getLoggedInUser).thenReturn(USER_ID);

            var auditorProvider = mongoBeanConfiguration.auditorProvider();

            assertThat(auditorProvider.getCurrentAuditor()).isEqualTo(Optional.of(USER_ID));
            assertThat(AuthenticationUtil.getLoggedInUser()).isEqualTo(USER_ID);
        }
    }
}
