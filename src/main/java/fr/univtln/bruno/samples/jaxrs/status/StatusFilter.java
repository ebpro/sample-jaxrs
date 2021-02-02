package fr.univtln.bruno.samples.jaxrs.status;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.annotation.Annotation;

@Provider
public class StatusFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        if (containerResponseContext.getStatus() == Response.Status.OK.getStatusCode()) {
            for (Annotation annotation : containerResponseContext.getEntityAnnotations()) {
                if (annotation instanceof Status) {
                    containerResponseContext.setStatus(((Status) annotation).value());
                    break;
                }
            }
        }
    }

}