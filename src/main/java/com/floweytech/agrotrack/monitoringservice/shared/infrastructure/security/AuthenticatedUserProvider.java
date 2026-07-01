package com.floweytech.agrotrack.monitoringservice.shared.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    public Long getUserId() {
        var claim = getJwt().getClaim("userId");
        if (claim instanceof Number number) {
            return number.longValue();
        }
        if (claim instanceof String value) {
            return Long.valueOf(value);
        }
        throw new IllegalStateException("Authenticated JWT does not contain a valid userId claim");
    }

    public String getRole() {
        return getJwt().getClaimAsString("role");
    }

    public boolean isAdministrator() {
        return "ROLE_ADMIN".equals(getRole()) || "ROLE_SUPER_ADMIN".equals(getRole());
    }

    private Jwt getJwt() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT is available");
        }
        return jwt;
    }
}
