package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.utils.AuthenticationUtils;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditConfiguration implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Set the value for the attributes annotated with @CreatedBy and @LastModifiedBy in BaseEntity
        return Optional.ofNullable(AuthenticationUtils.getLoggedInUser());
    }
}
