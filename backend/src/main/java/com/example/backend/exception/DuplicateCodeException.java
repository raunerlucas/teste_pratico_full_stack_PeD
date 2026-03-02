package com.example.backend.exception;

/**
 * Exception thrown when attempting to create or update a resource
 * with a code that already exists in the database.
 *
 * @author Equipe Backend
 * @version 1.0.0
 */
public class DuplicateCodeException extends RuntimeException {

    public DuplicateCodeException(String message) {
        super(message);
    }
}

