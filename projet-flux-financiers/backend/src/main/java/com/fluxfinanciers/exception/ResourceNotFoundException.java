package com.fluxfinanciers.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " avec l'id " + id + " est introuvable");
    }
}
