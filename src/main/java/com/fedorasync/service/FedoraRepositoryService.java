package com.fedorasync.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
     * Makes a single call to the REST service to retrieve all objects.
     * Parses JSON-LD response to extract IDs from the 'contains' property.
     *
     * @return List of all object IDs in the repository (just the last part after the last '/')
     * @throws IOException If an error occurs during retrieval
     */
    public List<String> getAllRepositoryIds() throws IOException {
        List<String> ids = new ArrayList<String>();

        try {
            // Fedora 5.1 - make a single call to get all objects
            String endpoint = "/";
            logger.debug("Fetching all objects from: {}", endpoint);
            String response = restClient.get(endpoint);

            JsonNode rootNode = objectMapper.readTree(response);
            
            // Handle both array and object responses
            JsonNode responseNode;
            if (rootNode.isArray()) {
                logger.debug("Response is an array, extracting first element");
                if (rootNode.size() > 0) {
                    responseNode = rootNode.get(0);
                } else {
                    logger.debug("Array is empty");
                    return ids;
                }
            } else if (rootNode.isObject()) {
                logger.debug("Response is an object");
                responseNode = rootNode;
            } else {
                logger.error("Response is neither an array nor an object");
                return ids;
            }
            
            // Debug: Log all keys in the response
            logger.debug("Response keys: {}", getAllKeys(responseNode));
            
            // Try multiple possible property names for the contains property
            JsonNode containsArray = findContainsProperty(responseNode);
            
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
                
                logger.debug("Retrieved {} objects total", ids.size());
            } else {
                logger.debug("No 'contains' property found or it's empty");
            }
        } catch (IOException e) {
            logger.error("Error retrieving objects", e);
            throw e;
        }

        logger.info("Total objects retrieved: {}", ids.size());
        return ids;
    }

    /**
     * Finds the 'contains' property in the JSON-LD response.
     * Tries multiple possible key variations since JSON-LD can use different formats.
     *
     * @param responseNode The root JSON node
     * @return The 'contains' array, or null if not found
     */
    private JsonNode findContainsProperty(JsonNode responseNode) {
        // List of possible property names for 'contains' in different JSON-LD formats
        String[] possibleContainsKeys = {
            "http://www.w3.org/ns/ldp#contains",  // Full namespace URI
            "ldp:contains",                        // Prefix format
            "contains",                            // Simple name
            "dc:contains",                         // Dublin Core variation
            "@contains"                            // JSON-LD context variation
        };
        
        for (String key : possibleContainsKeys) {
            if (responseNode.has(key)) {
                logger.debug("Found 'contains' property using key: {}", key);
                return responseNode.get(key);
            }
        }
        
        // If not found in standard places, iterate through all keys looking for one containing "contains"
        logger.debug("Standard 'contains' keys not found, searching through all keys...");
        Iterator<String> fieldNames = responseNode.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            if (key.contains("contains")) {
                logger.debug("Found potential 'contains' property with key: {}", key);
                return responseNode.get(key);
            }
        }
        
        return null;
    }

    /**
     * Gets all keys from a JSON node for debugging purposes.
     *
     * @param node The JSON node
     * @return A comma-separated string of all field names
     */
    private String getAllKeys(JsonNode node) {
        if (!node.isObject()) {
            return "Not an object";
        }
        
        StringBuilder keys = new StringBuilder();
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            if (keys.length() > 0) {
                keys.append(", ");
            }
            keys.append(fieldNames.next());
        }
        return keys.toString();
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
