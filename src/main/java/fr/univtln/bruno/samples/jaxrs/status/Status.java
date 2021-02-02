package fr.univtln.bruno.samples.jaxrs.status;

import jakarta.ws.rs.NameBinding;
import jakarta.ws.rs.core.Response;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Status {
    int CREATED = 201;
    int ACCEPTED = 202;
    int NO_CONTENT = 204;
    int RESET_CONTENT = 205;
    int PARTIAL_CONTENT = 206;

    int value();
}