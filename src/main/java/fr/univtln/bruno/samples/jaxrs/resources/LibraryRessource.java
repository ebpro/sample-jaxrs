package fr.univtln.bruno.samples.jaxrs.resources;

import fr.univtln.bruno.samples.jaxrs.model.Library;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.java.Log;

@Log
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Path("library")
public class LibraryRessource {
    @GET
    public Library getAuteurs() {
        return Library.demoLibrary;
    }
}
