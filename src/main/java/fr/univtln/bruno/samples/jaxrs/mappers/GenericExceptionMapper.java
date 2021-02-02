package fr.univtln.bruno.samples.jaxrs.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@SuppressWarnings("unused")
@Provider
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    final Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

    public Response toResponse(Exception ex) {
        return Response.status(status)
                .entity(ex.getMessage())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
