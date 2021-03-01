package fr.univtln.bruno.samples.jaxrs.exceptions;

import static jakarta.ws.rs.core.Response.*;

public class IllegalArgumentException extends BusinessException {
    public IllegalArgumentException() {
        super(Status.NOT_ACCEPTABLE);
    }
}
