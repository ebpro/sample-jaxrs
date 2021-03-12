package fr.univtln.bruno.samples.jaxrs.security.filter.request;

import fr.univtln.bruno.samples.jaxrs.security.MySecurityContext;
import fr.univtln.bruno.samples.jaxrs.security.annotations.BasicAuth;
import fr.univtln.bruno.samples.jaxrs.security.InMemoryLoginModule;
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
import jakarta.ws.rs.ext.Provider;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Authentication filter is a JAX-RS filter (@Provider with implements ContainerRequestFilter) is applied to every request whose method is annotated with @BasicAuth
 * as it is itself annotated with @BasicAuth (a personal annotation).
 * It performs authentication and check permissions against the acceded method with a basic authentication.
 */
@BasicAuth
@Provider
@Priority(Priorities.AUTHENTICATION)
@Log
public class BasicAuthenticationFilter implements ContainerRequestFilter {
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
        //if it is PermitAll access is granted
        //otherwise if it is DenyAll the access is refused
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

            //We extract the username and password encoded in base64
            final String encodedUserPassword = authorization.substring(AUTHENTICATION_SCHEME.length()).trim();

            //We Decode username and password (username:password)
            String[] usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword.getBytes())).split(":");

            final String username = usernameAndPassword[0];
            final String password = usernameAndPassword[1];

            log.info(username + " tries to log in");

            //We verify user access rights according to roles
            //After Authentication we are doing Authorization
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                EnumSet<InMemoryLoginModule.Role> rolesSet =
                        Arrays.stream(rolesAnnotation.value())
                                .map(InMemoryLoginModule.Role::valueOf)
                                .collect(Collectors.toCollection(() -> EnumSet.noneOf(InMemoryLoginModule.Role.class)));

                //We check to login/password
                if (!InMemoryLoginModule.USER_DATABASE.login(username, password)) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Wrong username or password").build());
                    return;
                }
                //We check if the role is allowed
                if (!InMemoryLoginModule.isInRoles(rolesSet, username))
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Roles not allowed").build());

                //We build a new SecurityContext Class to transmit the security data
                // for this login attempt to JAX-RS
                requestContext.setSecurityContext(MySecurityContext.newInstance(AUTHENTICATION_SCHEME, username));

            }
        }
    }

}