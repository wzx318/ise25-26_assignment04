package de.seuhd.campuscoffee.service;

import de.seuhd.campuscoffee.domain.exceptions.DuplicatePosNameException;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeMissingFieldsException;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.ports.PosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Application service that provides convenience methods for importing POS data from OpenStreetMap.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OsmImportService {
    private final PosService posService;

    /**
     * Import a POS from an OSM node ID and return the persisted Pos.
     */
    public Pos importFromOsmNode(Long nodeId) throws OsmNodeNotFoundException, OsmNodeMissingFieldsException, DuplicatePosNameException {
        log.info("Request to import POS from OSM node {} received", nodeId);
        if (nodeId == null || nodeId <= 0) throw new IllegalArgumentException("nodeId must be a positive number");

        try {
            Pos pos = posService.importFromOsmNode(nodeId);
            log.info("Imported POS '{}' (ID={}) from OSM node {}", pos.name(), pos.id(), nodeId);
            return pos;
        } catch (OsmNodeNotFoundException | OsmNodeMissingFieldsException | DuplicatePosNameException e) {
            log.error("Failed to import OSM node {}: {}", nodeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while importing OSM node {}: {}", nodeId, e.getMessage());
            throw new OsmNodeNotFoundException(nodeId);
        }
    }
}
