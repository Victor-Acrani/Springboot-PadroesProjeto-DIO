package br.com.acrani.labpadroesprojetospring.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Long id) {
        super("Resource not found! Id: " + id);
    }
}
