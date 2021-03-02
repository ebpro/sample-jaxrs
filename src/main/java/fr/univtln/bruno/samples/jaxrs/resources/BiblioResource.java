package fr.univtln.bruno.samples.jaxrs.resources;


import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import fr.univtln.bruno.samples.jaxrs.model.BiblioModel;
import fr.univtln.bruno.samples.jaxrs.model.BiblioModel.Auteur;
import fr.univtln.bruno.samples.jaxrs.status.Status;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.java.Log;

import java.security.SecureRandom;
import java.util.*;

@Log
// The Java class will be hosted at the URI path "/biblio"
@Path("biblio")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public class BiblioResource {
    private static final BiblioModel modeleBibliotheque = BiblioModel.of();

    private static final SecureRandom random = new SecureRandom();

    @SuppressWarnings("SameReturnValue")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        return "hello";
    }

    @PUT
    @Path("init")
    public int init() throws IllegalArgumentException {
        modeleBibliotheque.supprimerAuteurs();
        modeleBibliotheque.addAuteur(Auteur.builder().prenom("Alfred").nom("Martin").build());
        modeleBibliotheque.addAuteur(Auteur.builder().prenom("Marie").nom("Durand").build());
        return modeleBibliotheque.getAuteurSize();
    }

    @PUT
    @Path("init/{size:[0-9]+}")
    public int init(@PathParam("size") int size) throws IllegalArgumentException {
        modeleBibliotheque.supprimerAuteurs();
        for (int i = 0; i < size; i++)
            modeleBibliotheque.addAuteur(
                    Auteur.builder()
                            .prenom(randomString(random.nextInt(6) + 2))
                            .nom(randomString(random.nextInt(6) + 2)).build());
        return modeleBibliotheque.getAuteurSize();
    }

    private String randomString(int targetStringLength) {
        int letterA = 97;
        int letterZ = 122;


        return random.ints(letterA, letterZ + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
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
     * @param auteur The author to be added without its id.
     * @return The added author with its id.
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

    @GET
    @Path("auteurs/filter")
    public List<Auteur> getFilteredAuteurs(@QueryParam("nom") String nom,
                                           @QueryParam("prenom") String prenom,
                                           @QueryParam("biographie") String biographie,
                                           @HeaderParam("sortKey") @DefaultValue("nom") String sortKey) {
        PaginationInfo paginationInfo = PaginationInfo.builder()
                .nom(nom)
                .prenom(prenom)
                .biographie(biographie)
                .sortKey(sortKey)
                .build();
        log.info(paginationInfo.toString());
        return modeleBibliotheque.getWithFilter(paginationInfo);
    }

    @GET
    @Path("auteurs/page")
    public List<Auteur> getAuteursPage(@BeanParam PaginationInfo paginationInfo) {
        return modeleBibliotheque.getWithFilter(paginationInfo);
    }
}
