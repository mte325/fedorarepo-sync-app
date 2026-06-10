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

    public List<String> getAllRepositoryIds() throws IOException {
        List<String> ids = new ArrayList<String>();
        int page = 1;
        int pageSize = 100;
        boolean hasMore = true;

        while (hasMore) {
            try {
                String endpoint = String.format("/search?page=%d&pageSize=%d", page, pageSize);
                String response = restClient.get(endpoint);

                JsonNode responseNode = objectMapper.readTree(response);
                JsonNode results = responseNode.get("results");

                if (results != null && results.isArray()) {
                    for (JsonNode result : results) {
                        String id = result.get("id").asText();
                        ids.add(id);
                    }

                    JsonNode hasMoreNode = responseNode.get("hasMore");
                    hasMore = hasMoreNode != null && hasMoreNode.asBoolean();
                    page++;

                    logger.debug("Retrieved {} objects on page {}", results.size(), page - 1);
                } else {
                    hasMore = false;
                }
            } catch (IOException e) {
                logger.error("Error retrieving objects from page {}", page, e);
                hasMore = false;
            }
        }

        logger.info("Total objects retrieved: {}", ids.size());
        return ids;
    }

    public boolean deleteObject(String objectId) {
        logger.debug("Attempting to delete object: {}", objectId);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            String endpoint = "/rest/" + objectId;

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

    public void close() {
        restClient.close();
    }
}
