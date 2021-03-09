package fr.univtln.bruno.samples.jaxrs.resources;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * The Pagination information to be injected with @BeanPararm Filter Queries.
 * Each field is annotated with a JAX-RS parameter injection.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationInfo {
    @SuppressWarnings("FieldMayBeFinal")
    @QueryParam("page")
    @Builder.Default
    long page = 1;

    @SuppressWarnings("FieldMayBeFinal")
    @QueryParam("pageSize")
    @Builder.Default
    long pageSize = 10;

    @HeaderParam("sortKey")
    @DefaultValue("name")
    String sortKey;

    @QueryParam("name")
    String name;

    @QueryParam("firstname")
    String firstname;

    @QueryParam("biography")
    String biography;
}
