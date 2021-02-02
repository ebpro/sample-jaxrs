package fr.univtln.bruno.samples.jaxrs.model;

import fr.univtln.bruno.samples.jaxrs.exceptions.IllegalArgumentException;
import fr.univtln.bruno.samples.jaxrs.exceptions.NotFoundException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;

import java.io.Serializable;

@Log
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "of")
public class BiblioModel {
    private static long lastId = 0;
    MutableLongObjectMap<Auteur> auteurs = LongObjectMaps.mutable.empty();

    public Auteur addAuteur(Auteur auteur) throws IllegalArgumentException {
        if (auteur.id != 0) throw new IllegalArgumentException();
        auteur.id = ++lastId;
        auteurs.put(auteur.id, auteur);
        return auteur;
    }

    public Auteur updateAuteur(long id, Auteur auteur) throws NotFoundException, IllegalArgumentException {
        if (auteur.id != 0) throw new IllegalArgumentException();
        auteur.id = id;
        if (!auteurs.containsKey(id)) throw new NotFoundException();
        auteurs.put(id, auteur);
        return auteur;
    }

    public void removeAuteur(long id) throws NotFoundException {
        if (!auteurs.containsKey(id)) throw new NotFoundException();
        auteurs.remove(id);
    }

    public Auteur getAuteur(long id) throws NotFoundException {
        if (!auteurs.containsKey(id)) throw new NotFoundException();
        return auteurs.get(id);
    }

    public int getAuteurSize() {
        return auteurs.size();
    }

    public void supprimerAuteurs() {
        auteurs.clear();
        lastId = 0;
    }

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
