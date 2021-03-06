package fr.univtln.bruno.samples.jaxrs.server;

import lombok.extern.java.Log;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http2.Http2AddOn;
import org.glassfish.grizzly.http2.Http2Configuration;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
public class BiblioServer {
    /**
     * The constant BASE_URI.
     */
// Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:9998/mylibrary";

    public static final int TLS_PORT = 4443;

    /**
     * Main method.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        log.info("Rest server starting..." + BASE_URI);
        final HttpServer server = startServer();

        addTLSandHTTP2(server);

        //The server will be shutdown at the end of the program
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

        log.info(String.format("Application started.%n" +
                               "Stop the application using CTRL+C"));

        //We wait an infinite time.
        Thread.currentThread().join();
        server.shutdown();
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in demos package and add a logging feature to the server.
        Logger logger = Logger.getLogger(BiblioServer.class.getName());
        logger.setLevel(Level.FINE);

        final ResourceConfig rc = new ResourceConfig()
                .packages(true, "fr.univtln.bruno.samples.jaxrs")
                .register(new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, null));

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Adds a https (TLS) listener to secure connexion and adds http2 on this protocol.
     * @param httpServer
     * @return
     * @throws IOException
     */
    public static HttpServer addTLSandHTTP2(HttpServer httpServer) throws IOException {
        NetworkListener listener =
                new NetworkListener("TLS",
                        NetworkListener.DEFAULT_NETWORK_HOST,
                        TLS_PORT);
        listener.setSecure(true);

        // We add the certificate stored in a java keystore in src/main/resources/ssl
        // By default a self signed certificate is generated by maven (see pom.xml)
        SSLContextConfigurator sslContextConfigurator = new SSLContextConfigurator();
        sslContextConfigurator.setKeyStoreBytes(BiblioServer.class.getResourceAsStream("/ssl/cert.jks").readAllBytes());
        sslContextConfigurator.setKeyStorePass("storepass");

        listener.setSSLEngineConfig(new SSLEngineConfigurator(sslContextConfigurator, false, false, false));

        // Create default HTTP/2 configuration and provide it to the AddOn
        Http2Configuration configuration = Http2Configuration.builder().build();
        Http2AddOn http2Addon = new Http2AddOn(configuration);

        // Register the Addon.
        listener.registerAddOn(http2Addon);
        httpServer.addListener(listener);

        return httpServer;
    }

}
