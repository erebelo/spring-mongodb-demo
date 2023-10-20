package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.utils.AuthenticationUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditingConfiguration implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Sets the values for attributes annotated by @CreatedBy and @LastModifiedBy in BaseEntity
        return Optional.ofNullable(AuthenticationUtils.getLoggedInUser());
    }
}
