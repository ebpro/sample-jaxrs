package fr.univtln.bruno.samples.jaxrs.security;

import jakarta.ws.rs.core.SecurityContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.security.Principal;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "newInstance")
public class MySecurityContext implements SecurityContext {
    private final String authenticationScheme;
    private final String username;

    @Override
    public Principal getUserPrincipal() {
        return InMemoryLoginModule.USER_DATABASE.getUser(username);
    }

    @Override
    public boolean isUserInRole(String role) {
        return InMemoryLoginModule.USER_DATABASE.getUserRoles(username).contains(InMemoryLoginModule.Role.valueOf(role));
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return authenticationScheme;
    }
}
