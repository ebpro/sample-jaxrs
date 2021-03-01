package fr.univtln.bruno.samples.jaxrs.resources;


import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import fr.univtln.bruno.samples.jaxrs.model.BiblioModel;
import fr.univtln.bruno.samples.jaxrs.model.BiblioModel.Auteur;
import fr.univtln.bruno.samples.jaxrs.status.Status;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.java.Log;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log
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
        modeleBibliotheque.supprimerAuteurs();
        modeleBibliotheque.addAuteur(Auteur.builder().prenom("Alfred").nom("Martin").build());
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

    @GET
    @Path("auteurs/filter")
    public List<Auteur> getFilteredAuteurs(@QueryParam("nom") String nom, @QueryParam("prenom") String prenom, @QueryParam("biograpĥie") String biographie,
                                           @HeaderParam("sortKey") @DefaultValue("nom") String sortKey) {
        log.info("Sort Key: "+sortKey);
        //Demo purpose ONLY sorting have to be done in the model
        return modeleBibliotheque
                .stream()
                .filter(auteur -> nom == null || auteur.getNom().equalsIgnoreCase(nom))
                .filter(auteur -> prenom == null || auteur.getPrenom().equalsIgnoreCase(prenom))
                .filter(auteur -> biographie == null || auteur.getBiographie().contains(biographie))
                //We use the news Java 15 switch syntax and value
                .sorted(Comparator.comparing(auteur -> switch (sortKey) {
                    case "nom" -> auteur.getNom();
                    case "prenom" -> auteur.getPrenom();
                    default -> throw new InvalidParameterException();
                }))
                .collect(Collectors.toList());
    }


}
