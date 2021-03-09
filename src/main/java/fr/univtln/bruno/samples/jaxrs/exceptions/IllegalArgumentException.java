package fr.univtln.bruno.samples.jaxrs.exceptions;

import jakarta.xml.bind.annotation.XmlRootElement;

import static jakarta.ws.rs.core.Response.Status;

@XmlRootElement
public class IllegalArgumentException extends BusinessException {
    public IllegalArgumentException() {
        super(Status.NOT_ACCEPTABLE);
    }
}
