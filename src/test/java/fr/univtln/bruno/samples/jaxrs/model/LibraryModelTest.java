package fr.univtln.bruno.samples.jaxrs.model;

import fr.univtln.bruno.samples.jaxrs.exceptions.BusinessException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LibraryModelTest {
    private static final Library modeleBibliotheque = Library.newInstance();
    private static final Library.Author AUTHOR_1 = Library.Author.builder().firstname("Jean").name("Martin").build();
    private static final Library.Author AUTHOR_2 = Library.Author.builder().firstname("Marie").name("Durand").build();

    /**
     * Adds two authors before each tests.
     */
    @Before
    public void beforeEach() throws BusinessException {
        modeleBibliotheque.addAuthor(SerializationUtils.clone(AUTHOR_1));
        modeleBibliotheque.addAuthor(SerializationUtils.clone(AUTHOR_2));
    }

    @After
    public void afterEach() {
        modeleBibliotheque.removesAuthors();
    }

    @Test
    public void addAuteur() throws BusinessException {
        Library.Author author = Library.Author.builder().firstname("John").name("Doe").biography("My life").build();
        modeleBibliotheque.addAuthor(SerializationUtils.clone(author));
        assertThat(author, samePropertyValuesAs(modeleBibliotheque.getAuthor(3), "id"));
    }

    @Test(expected = NotFoundException.class)
    public void addAuteurException() throws BusinessException {
        Library.Author author = Library.Author.builder().firstname("John").name("Doe").build();
        modeleBibliotheque.addAuthor(SerializationUtils.clone(author));
        assertThat(author, samePropertyValuesAs(modeleBibliotheque.getAuthor(4), "id"));
    }

    @Test
    public void updateAuteur() throws BusinessException {
        Library.Author author = Library.Author.builder().firstname("John").name("Doe").build();
        modeleBibliotheque.updateAuteur(1, SerializationUtils.clone(author));
        assertThat(author, samePropertyValuesAs(modeleBibliotheque.getAuthor(1), "id"));
    }

    @Test
    public void removeAuteur() throws BusinessException {
        modeleBibliotheque.removeAuthor(1);
        assertEquals(1, modeleBibliotheque.getAuthorsNumber());
        assertEquals(2, modeleBibliotheque.getAuthors().values().iterator().next().getId());
    }

    @Test
    public void getAuteur() throws BusinessException {
        Library.Author author = modeleBibliotheque.getAuthor(1);
        assertThat(author, samePropertyValuesAs(AUTHOR_1,"id"));
    }

    @Test
    public void getAuteurSize() {
        assertEquals(2, modeleBibliotheque.getAuthorsNumber());
    }

    @Test
    public void supprimerAuteurs() {
        modeleBibliotheque.removesAuthors();
        assertEquals(0,modeleBibliotheque.getAuthorsNumber());
        assertEquals(0, modeleBibliotheque.getAuthors().size());
    }

    @Test
    public void of() {
        Library modeleBibliotheque1 = Library.newInstance();
        assertNotNull(modeleBibliotheque1);
    }

    @Test
    public void getAuteurs() {
        assertNotNull(modeleBibliotheque.getAuthors());
    }
}