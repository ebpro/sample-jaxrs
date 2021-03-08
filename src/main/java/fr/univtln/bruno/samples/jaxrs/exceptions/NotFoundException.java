package fr.univtln.bruno.samples.jaxrs.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NotFoundException extends BusinessException {
    public NotFoundException() {
        super(Response.Status.NOT_FOUND);
    }
}
