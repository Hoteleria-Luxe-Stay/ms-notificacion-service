package com.hotel.notificacion.helpers.exceptions;

import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String field, String message) {
        super(message);
        this.fieldErrors = Map.of(field, message);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
