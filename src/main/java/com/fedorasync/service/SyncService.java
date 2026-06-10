package com.fedorasync.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for synchronizing Fedora repository with user-provided ID list.
 * Compatible with Java 1.8
 */
public class SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);
    private final FedoraRepositoryService fedoraService;

    public SyncService(FedoraRepositoryService fedoraService) {
        this.fedoraService = fedoraService;
    }

    public SyncResult synchronize(List<String> repositoryIds, List<String> userIds) {
        logger.info("Starting synchronization process");
        logger.info("Repository IDs: {}, User IDs: {}", repositoryIds.size(), userIds.size());

        SyncResult result = new SyncResult(repositoryIds.size(), userIds.size());
        Set<String> userIdSet = new HashSet<String>(userIds);

        List<String> idsToDelete = new ArrayList<String>();
        for (String id : repositoryIds) {
            if (!userIdSet.contains(id)) {
                idsToDelete.add(id);
            }
        }

        logger.info("Found {} IDs to delete", idsToDelete.size());
        result.setIdsToDelete(idsToDelete);

        for (String id : idsToDelete) {
            logger.debug("Deleting ID: {}", id);
            if (fedoraService.deleteObject(id)) {
                result.incrementSuccessfullyDeleted();
            } else {
                result.incrementFailedDeletions();
                result.addFailedId(id);
            }
        }

        logger.info("Synchronization complete. Successfully deleted: {}, Failed: {}",
                result.getSuccessfullyDeleted(), result.getFailedDeletions());

        return result;
    }

    public static class SyncResult {
        private final int totalRepositoryIds;
        private final int totalUserIds;
        private List<String> idsToDelete;
        private int successfullyDeleted;
        private int failedDeletions;
        private final List<String> failedIds;

        public SyncResult(int totalRepositoryIds, int totalUserIds) {
            this.totalRepositoryIds = totalRepositoryIds;
            this.totalUserIds = totalUserIds;
            this.successfullyDeleted = 0;
            this.failedDeletions = 0;
            this.failedIds = new ArrayList<String>();
        }

        public int getTotalRepositoryIds() { 
            return totalRepositoryIds; 
        }
        
        public int getTotalUserIds() { 
            return totalUserIds; 
        }
        
        public List<String> getIdsToDelete() { 
            return idsToDelete; 
        }
        
        public void setIdsToDelete(List<String> idsToDelete) { 
            this.idsToDelete = idsToDelete; 
        }
        
        public int getSuccessfullyDeleted() { 
            return successfullyDeleted; 
        }
        
        public void incrementSuccessfullyDeleted() { 
            this.successfullyDeleted++; 
        }
        
        public int getFailedDeletions() { 
            return failedDeletions; 
        }
        
        public void incrementFailedDeletions() { 
            this.failedDeletions++; 
        }
        
        public List<String> getFailedIds() { 
            return failedIds; 
        }
        
        public void addFailedId(String id) { 
            this.failedIds.add(id); 
        }
    }
}
