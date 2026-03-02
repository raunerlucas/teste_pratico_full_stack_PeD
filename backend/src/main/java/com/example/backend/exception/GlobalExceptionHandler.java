package com.example.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── 404 — Resource Not Found ────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    // ── 409 — Duplicate Code ────────────────────────────────────────────────────

    @ExceptionHandler(DuplicateCodeException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateCode(DuplicateCodeException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    // ── 400 — Bad Request (IllegalArgument) ─────────────────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // ── 409 — Data Integrity Violation (FK, unique constraints at DB level) ─────

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = parseDataIntegrityMessage(ex);
        return buildResponse(HttpStatus.CONFLICT, "Conflict", message);
    }

    // ── 500 — Fallback ─────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        // Unwrap nested causes — Spring @Transactional may wrap our exceptions
        Throwable cause = unwrapCause(ex);

        if (cause instanceof DuplicateCodeException dup) {
            return buildResponse(HttpStatus.CONFLICT, "Conflict", dup.getMessage());
        }
        if (cause instanceof ResourceNotFoundException notFound) {
            return buildResponse(HttpStatus.NOT_FOUND, "Not Found", notFound.getMessage());
        }
        if (cause instanceof DataIntegrityViolationException dataEx) {
            String message = parseDataIntegrityMessage(dataEx);
            return buildResponse(HttpStatus.CONFLICT, "Conflict", message);
        }

        log.error("Unhandled exception", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later."
        );
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Walks the cause chain up to 10 levels deep looking for a known exception type.
     */
    private Throwable unwrapCause(Throwable ex) {
        Throwable current = ex;
        int depth = 0;
        while (current.getCause() != null && current.getCause() != current && depth < 10) {
            current = current.getCause();
            if (current instanceof DuplicateCodeException
                    || current instanceof ResourceNotFoundException
                    || current instanceof DataIntegrityViolationException) {
                return current;
            }
            depth++;
        }
        return ex;
    }

    /**
     * Parses a {@link DataIntegrityViolationException} and returns a user-friendly message.
     */
    private String parseDataIntegrityMessage(DataIntegrityViolationException ex) {
        String rootMessage = ex.getMostSpecificCause().getMessage();

        if (rootMessage != null) {
            String upper = rootMessage.toUpperCase();

            // Unique constraint violation (duplicate code)
            if (upper.contains("UNIQUE") || upper.contains("DUPLICATE") || upper.contains("PRIMARY_KEY")) {
                if (upper.contains("RAW_MATERIAL") || upper.contains("CODE")) {
                    return "A raw material with this code already exists. Please use a different code.";
                }
                if (upper.contains("PRODUCT")) {
                    return "A product with this code already exists. Please use a different code.";
                }
                return "A record with this identifier already exists. Please use a different value.";
            }

            // Referential integrity violation (FK)
            if (upper.contains("REFERENTIAL") || upper.contains("FOREIGN KEY") || upper.contains("FK_")) {
                if (upper.contains("RAW_MATERIAL") || upper.contains("PRODUCT_COMPOSITION")) {
                    return "This raw material cannot be deleted because it is used in one or more product compositions. Remove it from products first.";
                }
                return "This record cannot be deleted because it is referenced by other records.";
            }
        }

        return "A data integrity error occurred. Please check your data and try again.";
    }
}

