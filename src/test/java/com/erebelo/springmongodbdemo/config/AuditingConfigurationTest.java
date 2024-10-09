package com.erebelo.springmongodbdemo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import com.erebelo.springmongodbdemo.util.AuthenticationUtil;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class AuditingConfigurationTest {

    private final MockedStatic<AuthenticationUtil> mockedStatic = mockStatic(AuthenticationUtil.class);

    private static final String USER_ID = "12345";

    @BeforeEach
    void init() {
        mockedStatic.when(AuthenticationUtil::getLoggedInUser).thenReturn(USER_ID);
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
        assertThat(AuthenticationUtil.getLoggedInUser()).isEqualTo(USER_ID);
    }
}
