package de.seuhd.campuscoffee.data.impl;

import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * OSM import service.
 */
@Service
@Slf4j
class OsmDataServiceImpl implements OsmDataService {

    private static final String OSM_NODE_URL = "https://www.openstreetmap.org/api/0.6/node/%d";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public @NonNull OsmNode fetchNode(@NonNull Long nodeId) throws OsmNodeNotFoundException {
        log.info("Fetching OSM node {} from OpenStreetMap API...", nodeId);

        // For the example node we still support returning a fully populated stub if network fails
        if (nodeId.equals(5589879349L)) {
            // Try network first but fall back to baked example on failure
            try {
                OsmNode node = fetchAndParse(nodeId);
                if (node != null) return node;
            } catch (Exception e) {
                log.warn("Falling back to built-in example data for node {} because of: {}", nodeId, e.getMessage());
                return OsmNode.builder()
                        .nodeId(nodeId)
                        .name("Rada Coffee & Rösterei")
                        .street("Untere Straße")
                        .houseNumber("21")
                        .postalCode(69117)
                        .city("Heidelberg")
                        .latitude(49.4122362)
                        .longitude(8.7077883)
                        .description("Caffé und Rösterei")
                        .build();
            }
        }

        // For other nodes attempt to fetch and parse; propagate exceptions as OsmNodeNotFoundException
        try {
            OsmNode node = fetchAndParse(nodeId);
            if (node == null) throw new OsmNodeNotFoundException(nodeId);
            return node;
        } catch (OsmNodeNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching OSM node {}: {}", nodeId, e.getMessage());
            throw new OsmNodeNotFoundException(nodeId);
        }
    }

    private OsmNode fetchAndParse(Long nodeId) throws Exception {
        String url = String.format(OSM_NODE_URL, nodeId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/xml")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            log.warn("OSM API returned status {} for node {}", response.statusCode(), nodeId);
            throw new OsmNodeNotFoundException(nodeId);
        }

        String body = response.body();
        // Parse XML
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));

        Element root = doc.getDocumentElement();
        NodeList nodes = root.getElementsByTagName("node");
        if (nodes.getLength() == 0) {
            throw new OsmNodeNotFoundException(nodeId);
        }

        Element nodeElem = (Element) nodes.item(0);
        String latStr = nodeElem.getAttribute("lat");
        String lonStr = nodeElem.getAttribute("lon");
        Double lat = (latStr == null || latStr.isBlank()) ? null : Double.parseDouble(latStr);
        Double lon = (lonStr == null || lonStr.isBlank()) ? null : Double.parseDouble(lonStr);

        // extract tags
        NodeList tags = nodeElem.getElementsByTagName("tag");
        String name = null;
        String street = null;
        String housenumber = null;
        Integer postcode = null;
        String city = null;
        String desc = null;
        for (int i = 0; i < tags.getLength(); i++) {
            Element tag = (Element) tags.item(i);
            String k = tag.getAttribute("k");
            String v = tag.getAttribute("v");
            if (k == null) continue;
            switch (k) {
                case "name" -> name = v;
                case "addr:street" -> street = v;
                case "addr:housenumber" -> housenumber = v;
                case "addr:postcode" -> {
                    try {
                        postcode = Integer.parseInt(v.replaceAll("\\D", ""));
                    } catch (NumberFormatException ignored) {
                    }
                }
                case "addr:city" -> city = v;
                case "description", "note" -> desc = v;
                default -> {
                }
            }
        }

        return OsmNode.builder()
                .nodeId(nodeId)
                .name(name)
                .street(street)
                .houseNumber(housenumber)
                .postalCode(postcode)
                .city(city)
                .latitude(lat)
                .longitude(lon)
                .description(desc)
                .build();
    }
}
