package fr.univtln.bruno.samples.jaxrs.resources;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationInfo {
    @HeaderParam("sortKey")
    @DefaultValue("nom")
    String sortKey;

    @QueryParam("page")
    @Builder.Default
    long page = 1;

    @QueryParam("pageSize")
    @Builder.Default
    long pageSize = 10;

    @QueryParam("nom")
    String nom;

    @QueryParam("prenom")
    String prenom;

    @QueryParam("biographie")
    String biographie;
}
