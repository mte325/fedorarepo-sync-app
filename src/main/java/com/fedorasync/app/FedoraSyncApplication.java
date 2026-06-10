package com.fedorasync.app;

import com.fedorasync.model.SyncConfig;
import com.fedorasync.service.FedoraRepositoryService;
import com.fedorasync.service.SyncService;
import com.fedorasync.util.ConfigLoader;
import com.fedorasync.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Fedora Repository Sync Application.
 */
public class FedoraSyncApplication {

    private static final Logger logger = LoggerFactory.getLogger(FedoraSyncApplication.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("Fedora Repository Sync Application v1.0");
        logger.info("========================================");

        try {
            logger.info("Loading configuration...");
            SyncConfig config = ConfigLoader.loadConfiguration();
            logger.info("Configuration loaded successfully");
            logger.info("Fedora URL: {}", config.getFedoraUrl());
            logger.info("User IDs file: {}", config.getUserIdsFile());

            displayMenu();
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    performSync(config);
                    break;
                case "2":
                    displayRepositoryInfo(config);
                    break;
                case "3":
                    displayUserIds(config);
                    break;
                case "4":
                    logger.info("Exiting application");
                    System.exit(0);
                    break;
                default:
                    logger.warn("Invalid choice. Please try again.");
                    main(args);
            }
            scanner.close();

        } catch (Exception e) {
            logger.error("Fatal error occurred", e);
            System.exit(1);
        }
    }

    private static void displayMenu() {
        System.out.println();
        System.out.println("Please select an option:");
        System.out.println("1. Perform Repository Sync");
        System.out.println("2. Display Repository Information");
        System.out.println("3. Display User ID List");
        System.out.println("4. Exit");
        System.out.print("Enter your choice (1-4): ");
    }

    private static void performSync(SyncConfig config) {
        logger.info("Starting synchronization process...");
        try {
            FedoraRepositoryService fedoraService = new FedoraRepositoryService(config);
            SyncService syncService = new SyncService(fedoraService);

            logger.info("Loading user-provided IDs from file...");
            List<String> userIds = FileUtil.loadIdsFromFile(config.getUserIdsFile());
            logger.info("Loaded {} user IDs", userIds.size());

            logger.info("Retrieving all IDs from Fedora repository...");
            List<String> repositoryIds = fedoraService.getAllRepositoryIds();
            logger.info("Retrieved {} IDs from repository", repositoryIds.size());

            logger.info("Comparing and synchronizing...");
            SyncService.SyncResult result = syncService.synchronize(repositoryIds, userIds);

            displaySyncResults(result);

        } catch (Exception e) {
            logger.error("Error during synchronization", e);
        }
    }

    private static void displayRepositoryInfo(SyncConfig config) {
        logger.info("Retrieving repository information...");
        try {
            FedoraRepositoryService fedoraService = new FedoraRepositoryService(config);
            List<String> ids = fedoraService.getAllRepositoryIds();
            
            System.out.println();
            System.out.println("========== Repository Information ==========");
            System.out.println("Fedora URL: " + config.getFedoraUrl());
            System.out.println("Total Objects: " + ids.size());
            System.out.println("===========================================");
            System.out.println();

        } catch (Exception e) {
            logger.error("Error retrieving repository information", e);
        }
    }

    private static void displayUserIds(SyncConfig config) {
        logger.info("Loading user ID list...");
        try {
            List<String> userIds = FileUtil.loadIdsFromFile(config.getUserIdsFile());
            
            System.out.println();
            System.out.println("========== User ID List ==========");
            System.out.println("Total IDs: " + userIds.size());
            System.out.println("IDs:");
            for (String id : userIds) {
                System.out.println("  - " + id);
            }
            System.out.println("==================================");
            System.out.println();

        } catch (Exception e) {
            logger.error("Error loading user ID list", e);
        }
    }

    private static void displaySyncResults(SyncService.SyncResult result) {
        System.out.println();
        System.out.println("========== Synchronization Results ==========");
        System.out.println("Total Repository IDs: " + result.getTotalRepositoryIds());
        System.out.println("Total User IDs: " + result.getTotalUserIds());
        System.out.println("IDs to Delete: " + result.getIdsToDelete().size());
        System.out.println("Successfully Deleted: " + result.getSuccessfullyDeleted());
        System.out.println("Failed Deletions: " + result.getFailedDeletions());
        System.out.println("=============================================");
        System.out.println();

        if (!result.getFailedIds().isEmpty()) {
            System.out.println("Failed to delete the following IDs:");
            for (String id : result.getFailedIds()) {
                System.out.println("  - " + id);
            }
        }
    }
}
