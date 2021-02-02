package fr.univtln.bruno.samples.jaxrs;

import fr.univtln.bruno.samples.jaxrs.model.BiblioModel.Auteur;
import fr.univtln.bruno.samples.jaxrs.server.BiblioServer;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.junit.*;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * A simple junit integration test for A REST service.
 */
public class ServerIT {
    private static HttpServer httpServer;

    private static WebTarget webTarget;

    /**
     * Starts the application before the tests.
     *
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
     *
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
        webTarget.path("biblio/init").request().put(Entity.entity("", MediaType.TEXT_PLAIN));
    }

    /**
     * Clears the data after each tests.
     */
    @After
    public void afterEach() {
        webTarget.path("biblio/auteurs").request().delete();
    }

    @Test
    public void testHello() {
        String hello = webTarget.path("biblio").request(MediaType.TEXT_PLAIN).get(String.class);
        assertEquals("hello", hello);
    }

    /**
     * Tests to get a author by id in JSON.
     */
    @Test
    public void testGetAuteurJSON() {
        Auteur responseAuteur = webTarget.path("biblio/auteurs/1").request(MediaType.APPLICATION_JSON).get(Auteur.class);
        assertNotNull(responseAuteur);
        assertEquals("Jean", responseAuteur.getPrenom());
        assertEquals("Martin", responseAuteur.getNom());
    }

    /**
     * Tests to get a author by id in XML.
     */
    @Test
    public void testGetAuteurXML() {
        Auteur responseAuteur = webTarget.path("biblio/auteurs/1").request(MediaType.TEXT_XML).get(Auteur.class);
        assertNotNull(responseAuteur);
        assertEquals("Jean", responseAuteur.getPrenom());
        assertEquals("Martin", responseAuteur.getNom());
    }

    /**
     * Tests to get a author by id in JSON.
     */
    @Test
    public void testGetAuteurJSONNotFoundException() {
        Response response = webTarget.path("biblio/auteurs/10").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * Tests to get a collection of authors in JSON.
     */
    @Test
    public void testGetAuteurs() {
        @SuppressWarnings("unchecked")
        Collection<Auteur> responseAuteurs = webTarget.path("biblio/auteurs").request(MediaType.APPLICATION_JSON).get(Collection.class);
        assertEquals(2, responseAuteurs.size());
    }

    /**
     * Tests to clear authors.
     */
    @Test
    public void deleteAuteurs() {
        webTarget.path("biblio/auteurs").request().delete();
        @SuppressWarnings("unchecked")
        Collection<Auteur> responseAuteurs = webTarget.path("biblio/auteurs").request(MediaType.APPLICATION_JSON).get(Collection.class);
        assertEquals(0, responseAuteurs.size());
    }

    /**
     * Tests to add an author in JSON.
     */
    @Test
    public void addAuteur() {
        webTarget.path("biblio/auteurs")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity("{\"nom\":\"Smith\",\"prenom\":\"John\",\"biographie\":\"My life\"}", MediaType.APPLICATION_JSON));
        @SuppressWarnings("unchecked")
        Collection<Auteur> responseAuteurs = webTarget.path("biblio/auteurs").request(MediaType.APPLICATION_JSON).get(Collection.class);
        assertEquals(3, responseAuteurs.size());
        Auteur responseAuteur = webTarget.path("biblio/auteurs/3").request(MediaType.APPLICATION_JSON).get(Auteur.class);
        assertNotNull(responseAuteur);
        assertEquals("John", responseAuteur.getPrenom());
        assertEquals("Smith", responseAuteur.getNom());
        assertEquals("My life", responseAuteur.getBiographie());
    }

    /**
     * Tests update an author in JSON.
     */
    @Test
    public void updateAuteur() {
        webTarget.path("biblio/auteurs/1")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity("{\"nom\":\"Doe\",\"prenom\":\"Jim\",\"biographie\":\"My weird life\"}", MediaType.APPLICATION_JSON));
        Auteur responseAuteur = webTarget.path("biblio/auteurs/1").request(MediaType.APPLICATION_JSON).get(Auteur.class);
        assertNotNull(responseAuteur);
        assertEquals("Jim", responseAuteur.getPrenom());
        assertEquals("Doe", responseAuteur.getNom());
        assertEquals("My weird life", responseAuteur.getBiographie());
    }

    /**
     * Tests update an author in JSON.
     */
    @Test
    public void updateAuteurIllegalArgument() {
        Response response = webTarget.path("biblio/auteurs/1")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity("{\"id\":\"1\",\"nom\":\"Doe\",\"prenom\":\"Jim\",\"biographie\":\"My weird life\"}", MediaType.APPLICATION_JSON));
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
}
