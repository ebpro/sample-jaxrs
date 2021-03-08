package fr.univtln.bruno.samples.jaxrs.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

import java.io.Serializable;

/**
 * The type Business exception, used add HTTP (HATEOS) capacities to exceptions.
 */
@Getter
@JsonIgnoreProperties({"stackTrace"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)

@XmlRootElement
public class BusinessException extends Exception implements Serializable {
    /**
     * The Status.
     */
    Response.Status status;

    /**
     * Instantiates a new Business exception.
     *
     * @param status the status
     */
    public BusinessException(Response.Status status) {
        super(status.getReasonPhrase());
        this.status = status;
    }
}
