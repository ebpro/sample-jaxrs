package fr.univtln.bruno.samples.jaxrs.security;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@BasicAuth
@Provider
@Priority(Priorities.AUTHENTICATION)
@Log
/**
 * This class if a filter for JAX-RS to perform authentication and to check permissions against the acceded method.
 */
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    //We inject the data from the acceded resource.
    @Context
    private ResourceInfo resourceInfo;

    @SneakyThrows
    @Override
    public void filter(ContainerRequestContext requestContext) {
        //We use reflection on the acceded method to look for security annotations.
        Method method = resourceInfo.getResourceMethod();
        //if its PermitAll access is granted
        //otherwise if its DenyAll the access is refused
        if (!method.isAnnotationPresent(PermitAll.class)) {
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Access denied to all users").build());
                return;
            }

            //We get the authorization header
            final String authorization = requestContext.getHeaderString(AUTHORIZATION_PROPERTY);

            //We check the presence of the credentials
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Please provide your credentials").build());
                return;
            }

            //Get encoded username and password
            final String encodedUserPassword = authorization.substring(AUTHENTICATION_SCHEME.length()).trim();

            //Decode username and password
            String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword.getBytes()));

            //Split username and password tokens
            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            final String username = tokenizer.nextToken();
            final String password = tokenizer.nextToken();

            log.info(username + " tries to log in with " + password);

            //Verify user access
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                EnumSet<UserDatabase.Role> rolesSet =
                        Arrays.stream(rolesAnnotation.value())
                                .map(r -> UserDatabase.Role.valueOf(r))
                                .collect(Collectors.toCollection(() -> EnumSet.noneOf(UserDatabase.Role.class)));

                //We check to login/password
                if (!UserDatabase.USER_DATABASE.checkPassword(username, password)) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Wrong username or password").build());
                    return;
                }
                //We check if the role is allowed
                if (Collections.disjoint(rolesSet, UserDatabase.USER_DATABASE.getUserRoles(username))) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Roles not allowed").build());
                    return;
                }

                //We build a new securitycontext to transmit the security data to JAX-RS
                requestContext.setSecurityContext(new SecurityContext() {

                    @Override
                    public Principal getUserPrincipal() {
                        return UserDatabase.USER_DATABASE.getUser(username);
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return UserDatabase.USER_DATABASE.getUserRoles(username).contains(UserDatabase.Role.valueOf(role));
                    }

                    @Override
                    public boolean isSecure() {
                        return true;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return AUTHENTICATION_SCHEME;
                    }
                });


            }
        }
    }

}