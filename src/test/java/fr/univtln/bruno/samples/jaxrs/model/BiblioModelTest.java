package fr.univtln.bruno.samples.jaxrs.model;

import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BiblioModelTest {
    private static final BiblioModel modeleBibliotheque = BiblioModel.of();
    private static final BiblioModel.Auteur auteur1 = BiblioModel.Auteur.builder().prenom("Jean").nom("Martin").build();
    private static final BiblioModel.Auteur auteur2 = BiblioModel.Auteur.builder().prenom("Marie").nom("Durand").build();

    /**
     * Adds two authors before each tests.
     */
    @Before
    public void beforeEach() throws IllegalArgumentException {
        modeleBibliotheque.addAuteur(SerializationUtils.clone(auteur1));
        modeleBibliotheque.addAuteur(SerializationUtils.clone(auteur2));
    }

    @After
    public void afterEach() {
        modeleBibliotheque.supprimerAuteurs();
    }

    @Test
    public void addAuteur() throws IllegalArgumentException, NotFoundException {
        BiblioModel.Auteur auteur = BiblioModel.Auteur.builder().prenom("John").nom("Doe").biographie("My life").build();
        modeleBibliotheque.addAuteur(SerializationUtils.clone(auteur));
        assertThat(auteur, samePropertyValuesAs(modeleBibliotheque.getAuteur(3), "id"));
    }

    @Test(expected = NotFoundException.class)
    public void addAuteurException() throws IllegalArgumentException, NotFoundException {
        BiblioModel.Auteur auteur = BiblioModel.Auteur.builder().prenom("John").nom("Doe").build();
        modeleBibliotheque.addAuteur(SerializationUtils.clone(auteur));
        assertThat(auteur, samePropertyValuesAs(modeleBibliotheque.getAuteur(4), "id"));
    }

    @Test
    public void updateAuteur() throws IllegalArgumentException, NotFoundException {
        BiblioModel.Auteur auteur = BiblioModel.Auteur.builder().prenom("John").nom("Doe").build();
        modeleBibliotheque.updateAuteur(1, SerializationUtils.clone(auteur));
        assertThat(auteur, samePropertyValuesAs(modeleBibliotheque.getAuteur(1), "id"));
    }

    @Test
    public void removeAuteur() throws NotFoundException {
        modeleBibliotheque.removeAuteur(1);
        assertEquals(1, modeleBibliotheque.getAuteurSize());
        assertEquals(2, modeleBibliotheque.getAuteurs().values().iterator().next().getId());
    }

    @Test
    public void getAuteur() throws NotFoundException {
        BiblioModel.Auteur auteur = modeleBibliotheque.getAuteur(1);
        assertThat(auteur, samePropertyValuesAs(auteur1,"id"));
    }

    @Test
    public void getAuteurSize() {
        assertEquals(2, modeleBibliotheque.getAuteurSize());
    }

    @Test
    public void supprimerAuteurs() {
        modeleBibliotheque.supprimerAuteurs();
        assertEquals(0,modeleBibliotheque.getAuteurSize());
        assertEquals(0, modeleBibliotheque.getAuteurs().size());
    }

    @Test
    public void of() {
        BiblioModel modeleBibliotheque1 = modeleBibliotheque.of();
        assertNotNull(modeleBibliotheque1);
    }

    @Test
    public void getAuteurs() {
        assertNotNull(modeleBibliotheque.getAuteurs());
    }
}