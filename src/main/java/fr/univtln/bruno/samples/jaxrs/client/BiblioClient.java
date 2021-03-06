package fr.univtln.bruno.samples.jaxrs.client;

import fr.univtln.bruno.samples.jaxrs.model.BiblioModel.Auteur;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.java.Log;

/**
 * Created by bruno on 04/11/14.
 */

@Log
public class BiblioClient {
    public static void main(String[] args) {
        // create the client
        Client client = ClientBuilder.newClient();
        WebTarget webResource = client.target("http://localhost:9998/myapp");

        //Send a put with a String as response
        String responseInitAsString = webResource.path("biblio/init")
                .request().put(Entity.entity("", MediaType.TEXT_PLAIN), String.class);
        log.info(responseInitAsString);

        //Send a get and parse the response as a String
        String responseAuteursAsJsonString = webResource.path("biblio/auteurs")
                .request().get(String.class);
        log.info(responseAuteursAsJsonString);

        //Idem but the result is deserialised to an instance of Auteur
        Auteur auteur = webResource.path("biblio/auteurs/1")
                .request()
                .get(Auteur.class);
        log.info(auteur.toString());

        //Log in to get the token
        String email = "john.doe@nowhere.com";
        String password = "admin";
        String token = webResource.path("biblio/login")
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((email + ":" + password).getBytes()))
                .get(String.class);
        if (!token.isBlank()) {
            log.info("token received.");
            //We access a JWT protected URL with the token
            String result = webResource.path("biblio/secured")
                    .request()
                    .header("Authorization", "Bearer " + token)
                    .get(String.class);

            log.info(result);
        }


    }
}
