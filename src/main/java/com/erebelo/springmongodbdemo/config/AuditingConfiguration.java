package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.util.AuthenticationUtil;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditingConfiguration implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Sets the values for attributes annotated by @CreatedBy
        // and @LastModifiedBy in BaseEntity
        return Optional.ofNullable(AuthenticationUtil.getLoggedInUser());
    }
}
