package br.com.acrani.labpadroesprojetospring.services.exceptions;

public class DuplicatedDataException extends RuntimeException{

    public DuplicatedDataException(String message) {
        super(message);
    }
}
