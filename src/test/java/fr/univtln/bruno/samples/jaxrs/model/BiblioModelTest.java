package fr.univtln.bruno.samples.jaxrs.model;

import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

public class BiblioModelTest {
    private static final BiblioModel modeleBibliotheque = BiblioModel.of();

    /**
     * Adds two authors before each tests.
     */
    @Before
    public void beforeEach() throws IllegalArgumentException {
        modeleBibliotheque.addAuteur(BiblioModel.Auteur.builder().prenom("Jean").nom("Martin").build());
        modeleBibliotheque.addAuteur(BiblioModel.Auteur.builder().prenom("Marie").nom("Durand").build());
    }

    @Test
    public void addAuteur() throws IllegalArgumentException, NotFoundException {
        BiblioModel.Auteur auteur = BiblioModel.Auteur.builder().prenom("John").nom("Doe").build();
        modeleBibliotheque.addAuteur(auteur);
        assertThat(auteur, samePropertyValuesAs(modeleBibliotheque.getAuteur(3)));
    }

    @Test(expected = NotFoundException.class)
    public void addAuteurException() throws IllegalArgumentException, NotFoundException {
        BiblioModel.Auteur auteur = BiblioModel.Auteur.builder().prenom("John").nom("Doe").build();
        modeleBibliotheque.addAuteur(auteur);
        assertThat(auteur, samePropertyValuesAs(modeleBibliotheque.getAuteur(4)));
    }

    @Test
    public void updateAuteur() {
    }

    @Test
    public void removeAuteur() {
    }

    @Test
    public void getAuteur() {
    }

    @Test
    public void getAuteurSize() {
    }

    @Test
    public void supprimerAuteurs() {
    }

    @Test
    public void of() {
    }

    @Test
    public void getAuteurs() {
    }

    @Test
    public void setAuteurs() {
    }
}