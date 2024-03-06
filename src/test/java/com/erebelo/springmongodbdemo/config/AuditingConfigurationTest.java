package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.utils.AuthenticationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class AuditingConfigurationTest {

    private final MockedStatic<AuthenticationUtils> mockedStatic = mockStatic(AuthenticationUtils.class);

    private static final String USER_ID = "12345";

    @BeforeEach
    void init() {
        mockedStatic.when(AuthenticationUtils::getLoggedInUser).thenReturn(USER_ID);
    }

    @AfterEach
    void clear() {
        if (Objects.nonNull(mockedStatic)) {
            mockedStatic.close();
        }
    }

    @Test
    void testGetCurrentAuditor() {
        var auditingConfiguration = new AuditingConfiguration();

        Optional<String> currentAuditor = auditingConfiguration.getCurrentAuditor();

        assertThat(currentAuditor).isEqualTo(Optional.of(USER_ID));
        assertThat(AuthenticationUtils.getLoggedInUser()).isEqualTo(USER_ID);
    }
}
