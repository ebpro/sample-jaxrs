package fr.univtln.bruno.samples.jaxrs.server;

import lombok.extern.java.Log;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
public class BiblioServer {
    /**
     * The constant BASE_URI.
     */
// Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:9998/myapp";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in demos package and add a logging feature to the server.
        Logger logger = Logger.getLogger(BiblioServer.class.getName());

        final ResourceConfig rc = new ResourceConfig()
                .packages(true, "fr.univtln.bruno.samples.jaxrs")
                .register(new LoggingFeature(logger, Level.INFO, null, null));

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) throws InterruptedException {
        log.info("Rest server starting..." + BASE_URI);
        final HttpServer server = startServer();

        //The server will be shutdown at the end of the program
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

        log.info(String.format("Application started.%n" +
                "Stop the application using CTRL+C"));

        //We wait an infinite time.
        Thread.currentThread().join();
        server.shutdown();
    }
}
