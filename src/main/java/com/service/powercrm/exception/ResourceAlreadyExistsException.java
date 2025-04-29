package com.service.powercrm.exception;

import java.util.Map;

public class ResourceAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final Map<String, String> errors;

    public ResourceAlreadyExistsException(Map<String, String> errors) {
        super("Erro de recurso já existente.");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
