package fr.univtln.bruno.samples.jaxrs.model;

import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import fr.univtln.bruno.samples.jaxrs.resources.PaginationInfo;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.univtln.bruno.samples.jaxrs.model.BiblioModel.Field.valueOf;


/**
 * The type Biblio model.
 */
@Log
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "of")
public class BiblioModel {
    public enum Field {NOM, PRENOM, BIOGRAPHIE}

    private static final AtomicLong lastId = new AtomicLong(0);

    @Delegate
    final MutableLongObjectMap<Auteur> auteurs = LongObjectMaps.mutable.empty();

    /**
     * Add auteur auteur.
     *
     * @param auteur the auteur
     * @return the auteur
     * @throws IllegalArgumentException the illegal argument exception
     */
    public Auteur addAuteur(Auteur auteur) throws IllegalArgumentException {
        if (auteur.id != 0) throw new IllegalArgumentException();
        auteur.id = lastId.incrementAndGet();
        auteurs.put(auteur.id, auteur);
        return auteur;
    }

    /**
     * Update auteur auteur.
     *
     * @param id     the id
     * @param auteur the auteur
     * @return the auteur
     * @throws NotFoundException        the not found exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    public Auteur updateAuteur(long id, Auteur auteur) throws NotFoundException, IllegalArgumentException {
        if (auteur.id != 0) throw new IllegalArgumentException();
        auteur.id = id;
        if (!auteurs.containsKey(id)) throw new NotFoundException();
        auteurs.put(id, auteur);
        return auteur;
    }

    /**
     * Remove auteur.
     *
     * @param id the id
     * @throws NotFoundException the not found exception
     */
    public void removeAuteur(long id) throws NotFoundException {
        if (!auteurs.containsKey(id)) throw new NotFoundException();
        auteurs.remove(id);
    }

    /**
     * Gets auteur.
     *
     * @param id the id
     * @return the auteur
     * @throws NotFoundException the not found exception
     */
    public Auteur getAuteur(long id) throws NotFoundException {
        if (!auteurs.containsKey(id)) throw new NotFoundException();
        return auteurs.get(id);
    }

    /**
     * Gets auteur size.
     *
     * @return the auteur size
     */
    public int getAuteurSize() {
        return auteurs.size();
    }


    /**
     * Returns a sorted, filtered and paginated list of authors.
     *
     * @param paginationInfo the pagination info
     * @return the sorted, filtered page.
     */
    public List<Auteur> getWithFilter(PaginationInfo paginationInfo) {
        //We build a author stream, first we add sorting
        Stream<Auteur> auteurStream = auteurs.stream()
                .sorted(Comparator.comparing(auteur -> switch (valueOf(paginationInfo.getSortKey().toUpperCase())) {
                    case NOM -> auteur.getNom();
                    case PRENOM -> auteur.getPrenom();
                    default -> throw new InvalidParameterException();
                }));

        //The add filters according to parameters
        if (paginationInfo.getNom()!=null)
            auteurStream = auteurStream.filter(auteur -> auteur.getNom().equalsIgnoreCase(paginationInfo.getNom()));
        if (paginationInfo.getPrenom()!=null)
            auteurStream = auteurStream.filter(auteur -> auteur.getPrenom().equalsIgnoreCase(paginationInfo.getPrenom()));
        if (paginationInfo.getBiographie()!=null)
            auteurStream = auteurStream.filter(auteur -> auteur.getBiographie().contains(paginationInfo.getBiographie()));

        //Finally add pagination instructions.
        if ((paginationInfo.getPage() > 0) && (paginationInfo.getPageSize() > 0)) {
            auteurStream = auteurStream
                    .skip(paginationInfo.getPageSize() * (paginationInfo.getPage() - 1))
                    .limit(paginationInfo.getPageSize());
        }

        return auteurStream.collect(Collectors.toList());
    }

    public void supprimerAuteurs() {
        auteurs.clear();
        lastId.set(0);
    }

    /**
     * The type Auteur.
     */
    @Builder
    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor

    //To enable XML Serialization
    @XmlRootElement
    //Because getter are generated by lombok
    @XmlAccessorType(XmlAccessType.FIELD)
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString
    public static class Auteur implements Serializable {
        @XmlAttribute
        @EqualsAndHashCode.Include
        long id;

        String nom;

        String prenom;

        String biographie;
    }
}
