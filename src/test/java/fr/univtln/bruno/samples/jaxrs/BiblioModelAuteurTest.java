package fr.univtln.bruno.samples.jaxrs;

import fr.univtln.bruno.samples.jaxrs.model.BiblioModel;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BiblioModelAuteurTest {

    @Test
    public void AuteurCreationTest() {
        long id = 1;
        String nom = "Doe", prenom = "John", bio = "My life";
        BiblioModel.Auteur auteur = BiblioModel.Auteur.builder()
                .id(id)
                .nom(nom)
                .prenom(prenom)
                .biographie(bio)
                .build();
        assertThat(auteur, allOf(hasProperty("nom", is(nom)),
                hasProperty("prenom", is(prenom)),
                hasProperty("biographie", is(bio))));
    }

}
