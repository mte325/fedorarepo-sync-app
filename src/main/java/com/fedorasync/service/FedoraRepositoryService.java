package com.fedorasync.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fedorasync.client.FedoraRestClient;
import com.fedorasync.model.SyncConfig;

/**
 * Service for interacting with the Fedora Repository.
 * Handles retrieval and deletion of repository objects.
 * Compatible with Java 1.8
 * Fedora 5.1 compatible
 */
public class FedoraRepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(FedoraRepositoryService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final FedoraRestClient restClient;
    private final ObjectMapper objectMapper;
    private final SyncConfig config;

    public FedoraRepositoryService(SyncConfig config) {
        this.config = config;
        this.restClient = new FedoraRestClient(config);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retrieves all repository IDs using Fedora 5.1 REST API.
     * Uses pagination to handle large result sets.
     *
     * @return List of all object IDs in the repository
     * @throws IOException If an error occurs during retrieval
     */
    public List<String> getAllRepositoryIds() throws IOException {
        List<String> ids = new ArrayList<String>();
        int offset = 0;
        int limit = 100;
        boolean hasMore = true;

        while (hasMore) {
            try {
                // Fedora 5.1 uses offset and limit parameters instead of page
                String endpoint = String.format("/?offset=%d&limit=%d", offset, limit);
                logger.debug("Fetching objects with offset={}, limit={}", offset, limit);
                String response = restClient.get(endpoint);

                JsonNode responseNode = objectMapper.readTree(response);
                
                // Parse the RDF/XML or JSON response
                // The response structure depends on the Accept header
                // For simplicity, we'll look for common property patterns
                JsonNode contains = responseNode.get("contains");
                JsonNode children = responseNode.get("children");
                JsonNode members = responseNode.get("members");
                JsonNode results = responseNode.get("results");
                
                JsonNode itemsArray = null;
                if (contains != null && contains.isArray()) {
                    itemsArray = contains;
                } else if (children != null && children.isArray()) {
                    itemsArray = children;
                } else if (members != null && members.isArray()) {
                    itemsArray = members;
                } else if (results != null && results.isArray()) {
                    itemsArray = results;
                }

                if (itemsArray != null && itemsArray.size() > 0) {
                    for (JsonNode item : itemsArray) {
                        // Try to extract ID from different possible locations
                        String id = extractIdFromNode(item);
                        if (id != null && !id.isEmpty()) {
                            ids.add(id);
                            logger.debug("Found ID: {}", id);
                        }
                    }
                    
                    // Check if there are more results
                    hasMore = itemsArray.size() >= limit;
                    offset += limit;
                    
                    logger.debug("Retrieved {} objects, total so far: {}", itemsArray.size(), ids.size());
                } else {
                    hasMore = false;
                    logger.debug("No more objects found");
                }
            } catch (IOException e) {
                logger.error("Error retrieving objects with offset {}", offset, e);
                hasMore = false;
            }
        }

        logger.info("Total objects retrieved: {}", ids.size());
        return ids;
    }

    /**
     * Extracts the ID from a JSON node.
     * Tries multiple common property names.
     */
    private String extractIdFromNode(JsonNode node) {
        if (node == null) return null;
        
        // Try common ID property names
        String[] possibleIdProperties = {"@id", "id", "pid", "rdf:about", "uri"};
        
        for (String property : possibleIdProperties) {
            if (node.has(property)) {
                String value = node.get(property).asText();
                if (value != null && !value.isEmpty()) {
                    // Extract just the ID part if it's a full URI
                    if (value.contains("/")) {
                        return value.substring(value.lastIndexOf("/") + 1);
                    }
                    return value;
                }
            }
        }
        
        // If no ID found, return null
        return null;
    }

    /**
     * Deletes an object from the Fedora repository.
     * Retries on failure up to MAX_RETRIES times.
     *
     * @param objectId The ID of the object to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteObject(String objectId) {
        logger.debug("Attempting to delete object: {}", objectId);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            // Fedora 5.1 uses the root endpoint with object ID
            String endpoint = "/" + objectId;

            if (restClient.delete(endpoint)) {
                logger.info("Successfully deleted object: {}", objectId);
                return true;
            } else {
                logger.warn("Attempt {} to delete object {} failed", attempt, objectId);
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException e) {
                        logger.error("Retry delay interrupted", e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        logger.error("Failed to delete object {} after {} attempts", objectId, MAX_RETRIES);
        return false;
    }

    /**
     * Closes the REST client and releases resources.
     */
    public void close() {
        restClient.close();
    }
}
