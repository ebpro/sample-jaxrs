package fr.univtln.bruno.samples.jaxrs.resources;


import fr.univtln.bruno.samples.jaxrs.exceptions.BusinessException;
import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.model.Library;
import fr.univtln.bruno.samples.jaxrs.model.Library.Author;
import fr.univtln.bruno.samples.jaxrs.model.Library.Book;
import fr.univtln.bruno.samples.jaxrs.security.InMemoryLoginModule;
import fr.univtln.bruno.samples.jaxrs.security.User;
import fr.univtln.bruno.samples.jaxrs.security.annotations.BasicAuth;
import fr.univtln.bruno.samples.jaxrs.security.annotations.JWTAuth;
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
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * The Biblio resource.
 * A demo JAXRS class, that manages authors and offers a secured access.
 */
@Log
// The Java class will be hosted at the URI path "/biblio"
@Path("setup")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public class AdminResource {

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
    public int init() throws BusinessException {
        Library.demoLibrary.removesAuthors();
        Author author1 = Library.demoLibrary.addAuthor(Library.Author.builder().firstname("Alfred").name("Martin").build());
        Library.Author author2 = Library.demoLibrary.addAuthor(Author.builder().firstname("Marie").name("Durand").build());

        Library.demoLibrary.addBook(Book.builder().title("title1").authors(Set.of(author1)).build());
        Library.demoLibrary.addBook(Book.builder().title("title2").authors(Set.of(author1, author2)).build());
        Library.demoLibrary.addBook(Book.builder().title("title3").authors(Set.of(author2)).build());
        Library.demoLibrary.addBook(Book.builder().title("title4").authors(Set.of(author2)).build());

        return Library.demoLibrary.getAuthorsNumber();
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
    public int init(@PathParam("size") int size) throws BusinessException {
        Library.demoLibrary.removesAuthors();
        for (int i = 0; i < size; i++)
            Library.demoLibrary.addAuthor(
                    Author.builder()
                            .firstname(randomString(random.nextInt(6) + 2))
                            .name(randomString(random.nextInt(6) + 2)).build());
        return Library.demoLibrary.getAuthorsNumber();
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
