package fr.univtln.bruno.samples.jaxrs.resources;


import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import fr.univtln.bruno.samples.jaxrs.model.BiblioModel;
import fr.univtln.bruno.samples.jaxrs.model.BiblioModel.Auteur;
import fr.univtln.bruno.samples.jaxrs.status.Status;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Collection;

// The Java class will be hosted at the URI path "/biblio"
@Path("biblio")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public class BiblioResource {
    private static final BiblioModel modeleBibliotheque = BiblioModel.of();

    @SuppressWarnings("SameReturnValue")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        return "hello";
    }

    @PUT
    @Path("init")
    public int init() throws IllegalArgumentException {
        modeleBibliotheque.addAuteur(Auteur.builder().prenom("Jean").nom("Martin").build());
        modeleBibliotheque.addAuteur(Auteur.builder().prenom("Marie").nom("Durand").build());
        return modeleBibliotheque.getAuteurSize();
    }

    @PUT
    @Path("auteurs/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Auteur updateAuteur(@PathParam("id") long id, Auteur auteur) throws NotFoundException, IllegalArgumentException {
        return modeleBibliotheque.updateAuteur(id, auteur);
    }

    /**
     * Status annotation is a trick to fine tune 2XX status codes (see the status package).
     *
     * @param auteur
     * @return
     * @throws IllegalArgumentException
     */
    @POST
    @Status(Status.CREATED)
    @Path("auteurs")
    @Consumes(MediaType.APPLICATION_JSON)
    public Auteur ajouterAuteur(Auteur auteur) throws IllegalArgumentException {
        return modeleBibliotheque.addAuteur(auteur);
    }

    @DELETE
    @Path("auteurs/{id}")
    public void supprimerAuteur(@PathParam("id") final long id) throws NotFoundException {
        modeleBibliotheque.removeAuteur(id);
    }

    @DELETE
    @Path("auteurs")
    public void supprimerAuteurs() {
        modeleBibliotheque.supprimerAuteurs();
    }

    @GET
    @Path("auteurs/{id}")
    public Auteur getAuteur(@PathParam("id") final long id) throws NotFoundException {
        return modeleBibliotheque.getAuteur(id);
    }

    @GET
    @Path("auteurs")
    public Collection<Auteur> getAuteurs() {
        return modeleBibliotheque.getAuteurs().values();
    }
}
