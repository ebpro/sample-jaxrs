package fr.univtln.bruno.samples.jaxrs.mappers;

import fr.univtln.bruno.samples.jaxrs.exceptions.BusinessException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@SuppressWarnings("unused")
@Provider
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    public Response toResponse(BusinessException ex) {
        return Response.status(ex.getStatus())
                .entity(ex.getMessage())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
