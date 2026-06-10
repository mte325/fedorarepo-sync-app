package com.fedorasync.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations.
 * Handles reading IDs from files.
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Loads IDs from a file, one ID per line.
     *
     * @param filePath Path to the file containing IDs
     * @return List of IDs read from the file
     * @throws IOException If an error occurs while reading the file
     */
    public static List<String> loadIdsFromFile(String filePath) throws IOException {
        List<String> ids = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    ids.add(line);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading file: {}", filePath, e);
            throw e;
        }

        logger.info("Loaded {} IDs from file: {}", ids.size(), filePath);
        return ids;
    }

    /**
     * Saves IDs to a file, one ID per line.
     *
     * @param filePath Path to the file where IDs will be saved
     * @param ids List of IDs to save
     * @throws IOException If an error occurs while writing the file
     */
    public static void saveIdsToFile(String filePath, List<String> ids) throws IOException {
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            for (String id : ids) {
                writer.write(id);
                writer.write(System.lineSeparator());
            }
            logger.info("Saved {} IDs to file: {}", ids.size(), filePath);
        } catch (IOException e) {
            logger.error("Error writing file: {}", filePath, e);
            throw e;
        }
    }
}
