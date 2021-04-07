package fr.univtln.bruno.samples.jaxrs;

import fr.univtln.bruno.samples.jaxrs.model.Library;
import fr.univtln.bruno.samples.jaxrs.model.Library.Author;
import fr.univtln.bruno.samples.jaxrs.security.InMemoryLoginModule;
import fr.univtln.bruno.samples.jaxrs.server.BiblioServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.junit.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * A simple junit integration test for A REST service.
 */
public class ServerIT {
    private static HttpServer httpServer;

    private static WebTarget webTarget;

    /**
     * Starts the application before the tests.
     */
    @BeforeClass
    public static void setUp() {
        //start the Grizzly2 web container
        httpServer = BiblioServer.startServer();
        // create the client
        Client client = ClientBuilder.newClient();
        webTarget = client.target(BiblioServer.BASE_URI);
    }

    /**
     * Stops the application at the end of the test.
     */
    @AfterClass
    public static void tearDown() {
        httpServer.shutdown();
    }

    /**
     * Adds two authors before each tests.
     */
    @Before
    public void beforeEach() {
        webTarget.path("library/init").request().put(Entity.entity("", MediaType.TEXT_PLAIN));
    }

    /**
     * Clears the data after each tests.
     */
    @After
    public void afterEach() {
        webTarget.path("authors").request().delete();
    }

    @Test
    public void testHello() {
        String hello = webTarget.path("library/hello").request(MediaType.TEXT_PLAIN).get(String.class);
        assertEquals("hello", hello);
    }

    /**
     * Tests to get a author by id in JSON.
     */
    @Test
    public void testGetAuteurJSON() {
        Library.Author responseAuthor = webTarget.path("authors/1").request(MediaType.APPLICATION_JSON).get(Library.Author.class);
        assertNotNull(responseAuthor);
        assertEquals("Alfred", responseAuthor.getFirstname());
        assertEquals("Martin", responseAuthor.getName());
    }

    /**
     * Tests to get a author by id in XML.
     */
    @Test
    public void testGetAuteurXML() {
        Library.Author responseAuthor = webTarget.path("authors/1").request(MediaType.TEXT_XML).get(Library.Author.class);
        assertNotNull(responseAuthor);
        assertEquals("Alfred", responseAuthor.getFirstname());
        assertEquals("Martin", responseAuthor.getName());
    }

    /**
     * Tests to get a author by id in JSON.
     */
    @Test
    public void testGetAuteurJSONNotFoundException() {
        Response response = webTarget.path("authors/10").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * Tests to get a collection of authors in JSON.
     */
    @Test
    public void testGetAuteurs() {
        Collection<Library.Author> responseAuthors = webTarget.path("authors").request(MediaType.APPLICATION_JSON).get(new GenericType<>() {
        });
        assertEquals(2, responseAuthors.size());
    }

    /**
     * Tests to clear authors.
     */
    @Test
    public void deleteAuteurs() {
        webTarget.path("authors").request().delete();
        Collection<Library.Author> responseAuthors = webTarget.path("authors").request(MediaType.APPLICATION_JSON).get(new GenericType<>() {
        });
        assertEquals(0, responseAuthors.size());
    }

    /**
     * Tests to delete an author.
     */
    @Test
    public void deleteAuteur() {
        webTarget.path("authors/1").request().delete();
        Collection<Library.Author> responseAuthors = webTarget.path("authors").request(MediaType.APPLICATION_JSON).get(new GenericType<>() {
        });
        assertEquals(1, responseAuthors.size());
        assertEquals(2, responseAuthors.iterator().next().getId());
    }


    /**
     * Tests to add an author in JSON.
     */
    @Test
    public void addAuteur() {
        webTarget.path("authors")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity("{\"name\":\"Smith\",\"firstname\":\"John\",\"biography\":\"My life\"}", MediaType.APPLICATION_JSON));
        Collection<Author> responseAuthors = webTarget.path("authors").request(MediaType.APPLICATION_JSON).get(new GenericType<>() {
        });
        assertEquals(3, responseAuthors.size());
        Library.Author responseAuthor = webTarget.path("authors/3").request(MediaType.APPLICATION_JSON).get(Library.Author.class);
        assertNotNull(responseAuthor);
        assertEquals("John", responseAuthor.getFirstname());
        assertEquals("Smith", responseAuthor.getName());
        assertEquals("My life", responseAuthor.getBiography());
    }

    /**
     * Tests update an author in JSON.
     */
    @Test
    public void updateAuteur() {
        webTarget.path("authors/1")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity("{\"name\":\"Doe\",\"firstname\":\"Jim\",\"biography\":\"My weird life\"}", MediaType.APPLICATION_JSON));
        Author responseAuthor = webTarget.path("authors/1").request(MediaType.APPLICATION_JSON).get(Library.Author.class);
        assertNotNull(responseAuthor);
        assertEquals("Jim", responseAuthor.getFirstname());
        assertEquals("Doe", responseAuthor.getName());
        assertEquals("My weird life", responseAuthor.getBiography());
    }

    /**
     * Tests update an author in JSON.
     */
    @Test
    public void updateAuteurIllegalArgument() {
        Response response = webTarget.path("authors/1")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity("""
                        {"id":"1","name":"Doe","firstname":"Jim","biography":"My weird life"}""", MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus());
    }

    /**
     * Test if a WADL document is available at the relative path
     * "application.wadl".
     */
    @Test
    public void testApplicationWadl() {
        String serviceWadl = webTarget.path("application.wadl")
                .request(MediaTypes.WADL_TYPE)
                .get(String.class);
        assertTrue(serviceWadl.length() > 0);
    }

    /**
     * Tests filters and query param.
     */
    @Test
    public void filter() {
        List<Library.Author> authors = webTarget.path("authors/filter")
                .queryParam("firstname","Marie")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<>() {});

        assertEquals(1, authors.size());
        assertEquals("Marie", authors.get(0).getFirstname());
    }

    @Test
    public void refusedLogin() {
        Response result = webTarget.path("setup/login")
                .request()
                .get();
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void acceptedLogin() {
        String email="john.doe@nowhere.com";
        String password="admin";
        Response result = webTarget.path("setup/login")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .header("Authorization",  "Basic "+java.util.Base64.getEncoder().encodeToString((email+":"+password).getBytes()))
                .get();

        String entity = result.readEntity(String.class);
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(InMemoryLoginModule.KEY)
                .build()
                .parseClaimsJws(entity);
        assertEquals(email,jws.getBody().getSubject());
    }

    @Test
    public void jwtAccess() {
        //Log in to get the token
        String email="john.doe@nowhere.com";
        String password="admin";
        String token = webTarget.path("setup/login")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .header("Authorization",  "Basic "+java.util.Base64.getEncoder().encodeToString((email+":"+password).getBytes()))
                .get(String.class);

        //We access a JWT protected URL with the token
        Response result = webTarget.path("setup/secured")
                .request()
                .header( "Authorization",  "Bearer "+token)
                .get();
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals("Access with JWT ok for Doe, John <john.doe@nowhere.com>",result.readEntity(String.class));
    }

    @Test
    public void jwtAccessDenied() {
        String forgedToken = Jwts.builder()
                .setIssuer("sample-jaxrs")
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setSubject("john.doe@nowhere.com")
                .claim("firstname", "John")
                .claim("lastname", "Doe")
                .setExpiration(Date.from(LocalDateTime.now().plus(15, ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toInstant()))
                //A RANDOM KEY DIFFERENT FROM THE SERVER
                .signWith( Keys.secretKeyFor(SignatureAlgorithm.HS256)).compact();

        //We access a JWT protected URL with the token
        Response result = webTarget.path("setup/secured")
                .request()
                .header( "Authorization",  "Bearer "+forgedToken)
                .get();
        assertNotEquals(Response.Status.OK.getStatusCode(), result.getStatus());
    }

}
