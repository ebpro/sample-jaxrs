package fr.univtln.bruno.samples.jaxrs.resources;

import fr.univtln.bruno.samples.jaxrs.exceptions.BusinessException;
import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import fr.univtln.bruno.samples.jaxrs.model.Library;
import fr.univtln.bruno.samples.jaxrs.model.Page;
import fr.univtln.bruno.samples.jaxrs.status.Status;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.java.Log;

import java.util.Collection;

@Log
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Path("authors")
public class AuthorResource {
    /**
     * Update an author with an given id.
     *
     * @param id     the id injected from the path param "id"
     * @param author a injected author made from the JSON data (@Consumes) from body of the request. This author is forbidden to havce an Id.
     * @return The resulting author with its id.
     * @throws NotFoundException        is returned if no author has the "id".
     * @throws IllegalArgumentException is returned if an "id" is also given in the request body.
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Library.Author updateAuthor(@PathParam("id") long id, Library.Author author) throws BusinessException {
        return Library.demoLibrary.updateAuteur(id, author);
    }

    /**
     * Adds an new author to the data.
     * Status annotation is a trick to fine tune 2XX status codes (see the status package).
     *
     * @param author The author to be added without its id.
     * @return The added author with its id.
     * @throws IllegalArgumentException if the author has an explicit id (id!=0).
     */
    @POST
    @Status(Status.CREATED)
    @Consumes(MediaType.APPLICATION_JSON)
    public Library.Author addAuthor(Library.Author author) throws BusinessException {
        return Library.demoLibrary.addAuthor(author);
    }

    /**
     * Removes an author by id from the data.
     *
     * @param id the id of the author to remove
     * @throws NotFoundException is returned if no author has the "id".
     */
    @DELETE
    @Path("{id}")
    public void removeAuthor(@PathParam("id") final long id) throws BusinessException {
        Library.demoLibrary.removeAuthor(id);
    }

    /**
     * Removes every authors
     */
    @DELETE
    public void removeAuthors() {
        Library.demoLibrary.removesAuthors();
    }

    /**
     * Find and return an author by id with a GET on the path "biblio/auteurs/{id}" where  {id} is the needed id.
     * The path parameter "id" is injected with @PathParam.
     *
     * @param id the needed author id.
     * @return the auteur with id.
     * @throws NotFoundException is returned if no author has the "id".
     */
    @GET
    @Path("{id}")
    public Library.Author getAuthor(@PathParam("id") final long id) throws BusinessException {
        return Library.demoLibrary.getAuthor(id);
    }

    /**
     * Gets auteurs.
     *
     * @return the auteurs
     */
    @GET
    public Collection<Library.Author> getAuthors() {
        return Library.demoLibrary.getAuthors().values();
    }

    /**
     * Gets a list of "filtered" authors.
     *
     * @param name        an optional exact filter on the name.
     * @param firstname     an optional exact filter on the firstname.
     * @param biography an optional contains filter on the biography.
     * @param sortKey    the sort key (prenom or nom).
     * @return the filtered auteurs
     */
    @GET
    @Path("filter")
    public Page<Library.Author> getFilteredAuthors(@QueryParam("name") String name,
                                                   @QueryParam("firstname") String firstname,
                                                   @QueryParam("biography") String biography,
                                                   @HeaderParam("sortKey") @DefaultValue("name") String sortKey) {
        PaginationInfo paginationInfo = PaginationInfo.builder()
                .name(name)
                .firstname(firstname)
                .biography(biography)
                .sortKey(sortKey)
                .build();

        return Library.demoLibrary.getAuthorsWithFilter(paginationInfo);
    }

    /**
     * Gets a page of authors after applying a sort.
     *
     * @param paginationInfo the pagination info represented as a class injected with @BeanParam.
     * @return the page of authors.
     */
    @GET
    @Path("page")
    public Page<Library.Author> getAuthorsPage(@BeanParam PaginationInfo paginationInfo) {
        return Library.demoLibrary.getAuthorsWithFilter(paginationInfo);
    }

}
