Project Requirement Proposal 

Goal
Feature Goal: Implement a new feature that allows importing a Point-of-Sale (POS) from an existing OpenStreetMap (OSM) entry.
Deliverable: Java service method and API endpoint to import POS data (name, address, coordinates) from OSM XML (e.g., Node ID 5589879349, “Rada Coffee & Rösterei, Untere Straße 21”, coordinates 49.4122362, 8.7077883).
Success Definition: The new POS entry (“Rada Coffee & Rösterei”, Untere Straße 21, coordinates 49.4122362, 8.7077883) appears correctly in the system after providing a valid OSM node ID.

User Persona
Target User: System administrator or barista who manages CampusCoffee POS entries.
Use Case: Admin enters an OSM Node ID (e.g., 5589879349) to import shop details such as Rada Coffee & Rösterei, Untere Straße 21, 49.4122362, 8.7077883.
User Journey:
1.Admin opens the CampusCoffee POS management interface.
2. Enters the OSM Node ID 5589879349.
3. System fetches and parses OSM XML data (name, address, latitude/longitude).
4. New POS “Rada Coffee & Rösterei” is created and saved in the database with coordinates 49.4122362, 8.7077883.
Pain Points Addressed: Avoids manual data entry of name, address, and location information, and reduces errors.

Why
This feature saves time and ensures consistent and accurate location data by automatically importing verified details from OpenStreetMap (“Rada Coffee & Rösterei”, Untere Straße 21, coordinates 49.4122362, 8.7077883).
It integrates with the existing POS creation logic in the domain and data modules.

What
Add a new service in the application or domain layer that:
Accepts an OSM Node ID . 
Fetches XML data from the OpenStreetMap API
Parses key fields (e.g., name = "Rada Coffee & Rösterei", address = "Untere Straße 21", latitude = 49.4122362, longitude = 8.7077883)
Creates a new POS entity and persists it

Success Criteria
Given a valid OSM Node ID (e.g., 5589879349), a new POS appears in the database 
Imported data matches the OpenStreetMap entry (name, address, and coordinates as above)
Invalid IDs trigger meaningful error messages
Documentation & References
Refer to README.md for setup and example API calls
Use existing POS import logic as reference
Example OSM XML: Node 5589879349 (“Rada Coffee & Rösterei”, Untere Straße 21, 49.4122362, 8.7077883)

## Prompt for GenAI Tool (for Cursor)
“You are a senior Java Spring Boot engineer. Implement a new feature for the CampusCoffee project that imports a new POS entry based on an OpenStreetMap node (e.g., 5589879349 for ‘Rada Coffee & Rösterei’, Untere Straße 21, coordinates 49.4122362, 8.7077883). The project uses a ports-and-adapters architecture with modules api, application, data, and domain. Generate a service and API endpoint that fetch OSM XML data, parse it, and save a new POS (including name, address, and coordinates). Include error handling and basic logging.”

Explanation: I used the PRP template from the repository to create a structured prompt describing the new OSM POS import feature.
The prompt includes specific example data (“Rada Coffee & Rösterei”, Untere Straße 21, coordinates 49.4122362, 8.7077883) to give the GenAI tool clearer context.
The structure follows the PRP format, keeping the task concise while providing all required technical and contextual information.
No extra fileswere created to minimize workload.
