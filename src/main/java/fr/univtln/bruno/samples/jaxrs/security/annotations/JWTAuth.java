package fr.univtln.bruno.samples.jaxrs.security.annotations;

import jakarta.ws.rs.NameBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A annotation for method to be secured with Java Web Token (JWT)
 * @see fr.univtln.bruno.samples.jaxrs.security.filter.JsonWebTokenFilter
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface JWTAuth {
}
