package fr.univtln.bruno.samples.jaxrs.resources;


import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import fr.univtln.bruno.samples.jaxrs.model.BiblioModel;
import fr.univtln.bruno.samples.jaxrs.model.BiblioModel.Auteur;
import fr.univtln.bruno.samples.jaxrs.security.InMemoryLoginModule;
import fr.univtln.bruno.samples.jaxrs.security.User;
import fr.univtln.bruno.samples.jaxrs.security.annotations.BasicAuth;
import fr.univtln.bruno.samples.jaxrs.security.annotations.JWTAuth;
import fr.univtln.bruno.samples.jaxrs.status.Status;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.extern.java.Log;

import javax.naming.AuthenticationException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The Biblio resource.
 * A demo JAXRS class, that manages authors and offers a secured access.
 */
@Log
// The Java class will be hosted at the URI path "/biblio"
@Path("biblio")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public class BiblioResource {
    //A in memory instance of a Library model. Kind of a mock.
    private static final BiblioModel modeleBibliotheque = BiblioModel.of();

    //A random number generator
    private static final SecureRandom random = new SecureRandom();

    /**
     * The simpliest method that just return "hello" in plain text with GET on the default path "biblio".
     *
     * @return the string
     */
    @SuppressWarnings("SameReturnValue")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        return "hello";
    }

    /**
     * An init method that add two authors with a PUT on the default path.
     *
     * @return the number of generated authors.
     * @throws IllegalArgumentException the illegal argument exception
     */
    @PUT
    @Path("init")
    public int init() throws IllegalArgumentException {
        modeleBibliotheque.supprimerAuteurs();
        modeleBibliotheque.addAuteur(Auteur.builder().prenom("Alfred").nom("Martin").build());
        modeleBibliotheque.addAuteur(Auteur.builder().prenom("Marie").nom("Durand").build());
        return modeleBibliotheque.getAuteurSize();
    }

    /**
     * An init method that add a given number of random authors whose names are just random letters on PUT.
     * The number of authors if given in the path avec bound to the name size. The needed format (an integer) is checked with a regular expression [0-9]+
     * The parameter is injected with @PathParam
     *
     * @param size the number of authors to add
     * @return the int number of generated authors.
     * @throws IllegalArgumentException the illegal argument exception
     */
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

    /**
     * A random string generator
     *
     * @param targetStringLength the length of the String
     * @return
     */
    private String randomString(int targetStringLength) {
        int letterA = 97;
        int letterZ = 122;
        return random.ints(letterA, letterZ + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Update an author with an given id.
     *
     * @param id     the id injected from the path param "id"
     * @param auteur a injected author made from the JSON data (@Consumes) from body of the request. This author is forbidden to havce an Id.
     * @return The resulting author with its id.
     * @throws NotFoundException        is returned if no author has the "id".
     * @throws IllegalArgumentException is returned if an "id" is also given in the request body.
     */
    @PUT
    @Path("auteurs/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Auteur updateAuteur(@PathParam("id") long id, Auteur auteur) throws NotFoundException, IllegalArgumentException {
        return modeleBibliotheque.updateAuteur(id, auteur);
    }

    /**
     * Adds an new author to the data.
     * Status annotation is a trick to fine tune 2XX status codes (see the status package).
     *
     * @param auteur The author to be added without its id.
     * @return The added author with its id.
     * @throws IllegalArgumentException if the author has an explicit id (id!=0).
     */
    @POST
    @Status(Status.CREATED)
    @Path("auteurs")
    @Consumes(MediaType.APPLICATION_JSON)
    public Auteur ajouterAuteur(Auteur auteur) throws IllegalArgumentException {
        return modeleBibliotheque.addAuteur(auteur);
    }

    /**
     * Removes an author by id from the data.
     *
     * @param id the id of the author to remove
     * @throws NotFoundException is returned if no author has the "id".
     */
    @DELETE
    @Path("auteurs/{id}")
    public void supprimerAuteur(@PathParam("id") final long id) throws NotFoundException {
        modeleBibliotheque.removeAuteur(id);
    }

    /**
     * Removes every authors
     */
    @DELETE
    @Path("auteurs")
    public void supprimerAuteurs() {
        modeleBibliotheque.supprimerAuteurs();
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
    @Path("auteurs/{id}")
    public Auteur getAuteur(@PathParam("id") final long id) throws NotFoundException {
        return modeleBibliotheque.getAuteur(id);
    }

    /**
     * Gets auteurs.
     *
     * @return the auteurs
     */
    @GET
    @Path("auteurs")
    public Collection<Auteur> getAuteurs() {
        return modeleBibliotheque.getAuteurs().values();
    }

    /**
     * Gets a list of "filtered" authors.
     *
     * @param nom        an optional exact filter on the name.
     * @param prenom     an optional exact filter on the firstname.
     * @param biographie an optional contains filter on the biography.
     * @param sortKey    the sort key (prenom or nom).
     * @return the filtered auteurs
     */
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

    /**
     * Gets a page of authors after applying a sort.
     *
     * @param paginationInfo the pagination info represented as a class injected with @BeanParam.
     * @return the page of authors.
     */
    @GET
    @Path("auteurs/page")
    public List<Auteur> getAuteursPage(@BeanParam PaginationInfo paginationInfo) {
        return modeleBibliotheque.getWithFilter(paginationInfo);
    }

    /**
     * A GET method to access the context of the request : The URI, the HTTP headers, the request and the security context (needs authentication see below).
     *
     * @param uriInfo         the uri info
     * @param httpHeaders     the http headers
     * @param request         the request
     * @param securityContext the security context
     * @return A string representation of the available data.
     */
    @GET
    @Path("context")
    public String getContext(@Context UriInfo uriInfo, @Context HttpHeaders httpHeaders, @Context Request request, @Context SecurityContext securityContext) {
        String result = "UriInfo: (" + uriInfo.getRequestUri().toString() + ")\n"
                        + "Method: ("+request.getMethod()+")\n"
                        + "HttpHeaders(" + httpHeaders.getRequestHeaders().toString() + ")\n";

        if (securityContext != null) {
            result += " SecurityContext(Auth.scheme: [" + securityContext.getAuthenticationScheme() + "] \n";
            if (securityContext.getUserPrincipal() != null)
                result += "    user: [" + securityContext.getUserPrincipal().getName() + "] \n";
            result += "    secured: [" + securityContext.isSecure() + "] )";
        }
        return result;
    }

    /**
     * A GET restricted to ADMIN role with basic authentication.
     * @see fr.univtln.bruno.samples.jaxrs.security.filter.BasicAuthenticationFilter
     *
     * @param securityContext the security context
     * @return the restricted to admins
     */
    @GET
    @Path("adminsonly")
    @RolesAllowed("ADMIN")
    @BasicAuth
    public String getRestrictedToAdmins(@Context SecurityContext securityContext) {
        return "secret for admins !" + securityContext.getUserPrincipal().getName();
    }

    /**
     * A GET restricted to USER role with basic authentication (and not ADMIN !).
     * @see fr.univtln.bruno.samples.jaxrs.security.filter.BasicAuthenticationFilter
     *
     * @param securityContext the security context
     * @return the restricted to users
     */
    @GET
    @Path("usersonly")
    @RolesAllowed("USER")
    @BasicAuth
    public String getRestrictedToUsers(@Context SecurityContext securityContext) {
        return "secret for users ! to " + securityContext.getUserPrincipal().getName();
    }

    /**
     * A GET restricted to USER & ADMIN roles, secured with a JWT Token.
     * @see fr.univtln.bruno.samples.jaxrs.security.filter.JsonWebTokenFilter
     *
     * @param securityContext the security context
     * @return the string
     */
    @GET
    @Path("secured")
    @RolesAllowed({"USER", "ADMIN"})
    @JWTAuth
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public String securedByJWT(@Context SecurityContext securityContext) {
        log.info("USER ACCESS :" + securityContext.getUserPrincipal().getName());
        return "Access with JWT ok for " + securityContext.getUserPrincipal().getName();
    }

    /**
     * A GET restricted to ADMIN roles, secured with a JWT Token.
     *
     * @param securityContext the security context
     * @return the string
     */
    @GET
    @Path("secured/admin")
    @RolesAllowed({"ADMIN"})
    @JWTAuth
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public String securedByJWTAdminOnly(@Context SecurityContext securityContext) {
        log.info("ADMIN ACCESS :" + securityContext.getUserPrincipal().getName());
        return "Access with JWT ok for " + securityContext.getUserPrincipal().getName();
    }

    /**
     * a GET method to obtain a JWT token with basic authentication for USER and ADMIN roles.
     *
     * @param securityContext the security context
     * @return the base64 encoded JWT Token.
     */
    @GET
    @Path("login")
    @RolesAllowed({"USER", "ADMIN"})
    @BasicAuth
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public String login(@Context SecurityContext securityContext) {
        if (securityContext.isSecure() && securityContext.getUserPrincipal() instanceof User) {
            User user = (User) securityContext.getUserPrincipal();
            return Jwts.builder()
                    .setIssuer("sample-jaxrs")
                    .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                    .setSubject(user.getEmail())
                    .claim("firstname", user.getFirstName())
                    .claim("lastname", user.getLastName())
                    .claim("roles", user.getRoles())
                    .setExpiration(Date.from(LocalDateTime.now().plus(15, ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toInstant()))
                    .signWith(InMemoryLoginModule.KEY).compact();
        }
        throw new WebApplicationException(new AuthenticationException());
    }
}
