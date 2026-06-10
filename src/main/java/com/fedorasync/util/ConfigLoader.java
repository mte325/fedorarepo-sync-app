package com.fedorasync.util;

import com.fedorasync.model.SyncConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading configuration from properties file.
 * Reads configuration from application.properties file.
 */
public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String CONFIG_FILE = "application.properties";

    /**
     * Loads configuration from the application.properties file.
     *
     * @return SyncConfig object with loaded configuration
     * @throws IOException If configuration file cannot be read
     */
    public static SyncConfig loadConfiguration() throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            logger.info("Configuration loaded from {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", CONFIG_FILE, e);
            throw e;
        }

        // Extract properties with defaults
        String fedoraUrl = properties.getProperty("fedora.url", "http://localhost:8080/fcrepo/rest");
        String username = properties.getProperty("fedora.username", "admin");
        String password = properties.getProperty("fedora.password", "password");
        String apiVersion = properties.getProperty("fedora.api.version", "v1");
        String userIdsFile = properties.getProperty("user.ids.file", "user_ids.txt");
        String loggingLevel = properties.getProperty("logging.level", "INFO");

        logger.info("Configuration loaded - Fedora URL: {}", fedoraUrl);
        logger.info("User IDs file: {}", userIdsFile);

        return new SyncConfig(fedoraUrl, username, password, apiVersion, userIdsFile, loggingLevel);
    }

    /**
     * Loads configuration with a custom config file path.
     *
     * @param configFilePath Path to the custom configuration file
     * @return SyncConfig object with loaded configuration
     * @throws IOException If configuration file cannot be read
     */
    public static SyncConfig loadConfiguration(String configFilePath) throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            properties.load(fis);
            logger.info("Configuration loaded from {}", configFilePath);
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", configFilePath, e);
            throw e;
        }

        // Extract properties with defaults
        String fedoraUrl = properties.getProperty("fedora.url", "http://localhost:8080/fcrepo/rest");
        String username = properties.getProperty("fedora.username", "admin");
        String password = properties.getProperty("fedora.password", "password");
        String apiVersion = properties.getProperty("fedora.api.version", "v1");
        String userIdsFile = properties.getProperty("user.ids.file", "user_ids.txt");
        String loggingLevel = properties.getProperty("logging.level", "INFO");

        logger.info("Configuration loaded - Fedora URL: {}", fedoraUrl);
        logger.info("User IDs file: {}", userIdsFile);

        return new SyncConfig(fedoraUrl, username, password, apiVersion, userIdsFile, loggingLevel);
    }
}
