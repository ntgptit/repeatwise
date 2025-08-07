package com.repeatwise.util;

import com.repeatwise.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public final class ServiceUtils {
    
    private ServiceUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Safely finds an entity by ID and throws ResourceNotFoundException if not found
     * 
     * @param finder Supplier that returns Optional of the entity
     * @param entityName Name of the entity for error message
     * @param id ID of the entity
     * @return The found entity
     * @throws ResourceNotFoundException if entity not found
     */
    public static <T> T findEntityOrThrow(Supplier<Optional<T>> finder, String entityName, UUID id) {
        return finder.get()
                .orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with ID: " + id));
    }
    
    /**
     * Safely finds an entity by ID and user ID and throws ResourceNotFoundException if not found
     * 
     * @param finder Supplier that returns Optional of the entity
     * @param entityName Name of the entity for error message
     * @param id ID of the entity
     * @param userId ID of the user
     * @return The found entity
     * @throws ResourceNotFoundException if entity not found
     */
    public static <T> T findEntityOrThrow(Supplier<Optional<T>> finder, String entityName, UUID id, UUID userId) {
        return finder.get()
                .orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with ID: " + id + " for user ID: " + userId));
    }
    
    /**
     * Logs the start of a service operation
     * 
     * @param operation Description of the operation
     * @param params Parameters for the operation
     */
    public static void logOperationStart(String operation, Object... params) {
        log.info("Starting {}: {}", operation, String.join(", ", 
                java.util.Arrays.stream(params)
                        .map(Object::toString)
                        .toArray(String[]::new)));
    }
    
    /**
     * Logs the successful completion of a service operation
     * 
     * @param operation Description of the operation
     * @param result Result of the operation
     */
    public static void logOperationSuccess(String operation, Object result) {
        log.info("{} completed successfully: {}", operation, result);
    }
    
    /**
     * Logs a debug message for entity lookup
     * 
     * @param entityName Name of the entity
     * @param id ID of the entity
     */
    public static void logEntityLookup(String entityName, UUID id) {
        log.debug("Finding {} by ID: {}", entityName, id);
    }
    
    /**
     * Logs a debug message for entity lookup with additional parameters
     * 
     * @param entityName Name of the entity
     * @param params Parameters for the lookup
     */
    public static void logEntityLookup(String entityName, Object... params) {
        log.debug("Finding {}: {}", entityName, String.join(", ", 
                java.util.Arrays.stream(params)
                        .map(Object::toString)
                        .toArray(String[]::new)));
    }
} 