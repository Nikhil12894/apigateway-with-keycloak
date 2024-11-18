package com.nk.base.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nk.base.constants.ErrorCodeCommon;
import com.nk.base.dto.ErrorMetadata;
import com.nk.base.exception.CustomException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ErrorMetadataLoader {

    private final ObjectMapper objectMapper;

    private final Map<String, ErrorMetadata> errorMetadataMap;

    public ErrorMetadataLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.errorMetadataMap = new HashMap<>();
    }

    @Value("classpath:errors.json") // Default error metadata file
    private Resource defaultErrorResource;

    @Value("${custom.error.file:}") // Custom error metadata file (optional)
    private Resource customErrorResource;

    @PostConstruct
    public void loadErrorMetadata() {
        try {
            log.info("Loading error metadata...");
            log.debug("Default error file: {}", defaultErrorResource);
            // Load default errors.json
            if (defaultErrorResource.exists()) {
                List<ErrorMetadata> defaultErrors = objectMapper.readValue(
                        defaultErrorResource.getInputStream(),
                        new TypeReference<List<ErrorMetadata>>() {
                        }
                );
                defaultErrors.forEach(error -> errorMetadataMap.put(error.errorCode(), error));
            }
            log.debug("Error metadata map: {}", errorMetadataMap);
            // Load custom-errors.json if provided
            if (customErrorResource != null && customErrorResource.exists()) {
                List<ErrorMetadata> customErrors = objectMapper.readValue(
                        customErrorResource.getInputStream(),
                        new TypeReference<List<ErrorMetadata>>() {
                        }
                );
                customErrors.forEach(error -> errorMetadataMap.put(error.errorCode(), error)); // Overwrite default errors if conflict
            }
            log.debug("Custom error file: {}", customErrorResource);
            log.info("Loaded {} error metadata entries", errorMetadataMap.size());

        } catch (Exception e) {
            throw new CustomException(ErrorCodeCommon.UNKNOWN_ERROR, e.getMessage());
        }
    }

    public ErrorMetadata getErrorMetadata(String errorCode) {
        return errorMetadataMap.getOrDefault(errorCode, new ErrorMetadata(
                "UNKNOWN_ERROR",
                "An unknown error occurred.",
                500,
                "No additional details available."
        ));
    }
}
