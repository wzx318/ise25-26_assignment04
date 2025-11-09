package de.seuhd.campuscoffee.domain.model;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents an OpenStreetMap node with relevant Point of Sale information.
 * This is the domain model for OSM data before it is converted to a POS object.
 *
 * @param nodeId The OpenStreetMap node ID.
 */
@Builder
public record OsmNode(@NonNull Long nodeId,
                      @Nullable String name,
                      @Nullable String street,
                      @Nullable String houseNumber,
                      @Nullable Integer postalCode,
                      @Nullable String city,
                      @Nullable Double latitude,
                      @Nullable Double longitude,
                      @Nullable String description) {
    // Fields are nullable because not every OSM node provides all address tags.
}
