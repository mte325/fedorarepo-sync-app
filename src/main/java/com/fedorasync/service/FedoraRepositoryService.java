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
     * Parses JSON-LD response to extract IDs from the 'contains' property.
     *
     * @return List of all object IDs in the repository (just the last part after the last '/')
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
                
                // Parse the JSON-LD response looking for the 'contains' property
                JsonNode containsArray = responseNode.get("http://www.w3.org/ns/ldp#contains");
                
                if (containsArray != null && containsArray.isArray() && containsArray.size() > 0) {
                    logger.debug("Found {} items in 'contains' property", containsArray.size());
                    
                    for (JsonNode item : containsArray) {
                        // Extract the @id from each item
                        JsonNode idNode = item.get("@id");
                        if (idNode != null) {
                            String fullId = idNode.asText();
                            // Extract just the last part after the last '/'
                            String shortId = extractIdFromUrl(fullId);
                            if (shortId != null && !shortId.isEmpty()) {
                                ids.add(shortId);
                                logger.debug("Extracted ID: {}", shortId);
                            }
                        }
                    }
                    
                    // Check if there are more results
                    hasMore = containsArray.size() >= limit;
                    offset += limit;
                    
                    logger.debug("Retrieved {} objects, total so far: {}", containsArray.size(), ids.size());
                } else {
                    logger.debug("No 'contains' property found or it's empty");
                    hasMore = false;
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
     * Extracts the ID from a full URL.
     * Gets only the part after the last '/'.
     * Example: http://example.com/rest/CNEAI/2f972be7-21cb-4969-abe3-07b834150d5d
     *          -> 2f972be7-21cb-4969-abe3-07b834150d5d
     *
     * @param fullUrl The full URL
     * @return The ID part (text after the last '/'), or null if not found
     */
    private String extractIdFromUrl(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) {
            return null;
        }
        
        int lastSlashIndex = fullUrl.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < fullUrl.length() - 1) {
            return fullUrl.substring(lastSlashIndex + 1);
        }
        
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
