package fr.univtln.bruno.samples.jaxrs.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.stream.Collectors;

@JWTAuth
@Provider
@Priority(Priorities.AUTHENTICATION)
@Log
/**
 * This class if a filter for JAX-RS to perform authentication via JWT.
 */
public class JsonWebTokenFilter implements ContainerRequestFilter {
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

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


            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                EnumSet<UserDatabase.Role> rolesSet =
                        Arrays.stream(rolesAnnotation.value())
                                .map(r -> UserDatabase.Role.valueOf(r))
                                .collect(Collectors.toCollection(() -> EnumSet.noneOf(UserDatabase.Role.class)));


                //We check the credentials presence
                if (authorization == null || authorization.isEmpty()) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Please provide your credentials").build());
                    return;
                }

                //Gets the token
                log.info("AUTH: "+authorization);
                final String compactJwt = authorization.substring(AUTHENTICATION_SCHEME.length()).trim();
                if (!authorization.contains(AUTHENTICATION_SCHEME) || compactJwt == null || compactJwt.isEmpty()) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Please provide your credentials").build());
                    return;
                }
                log.info("JWT: "+compactJwt);

                Jws<Claims> jws = Jwts.parserBuilder()
                        .setSigningKey(UserDatabase.KEY)
                        .build()
                        .parseClaimsJws(compactJwt);
                log.info("JWT decoded: "+jws.toString());

                final String username = jws.getBody().getSubject();

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