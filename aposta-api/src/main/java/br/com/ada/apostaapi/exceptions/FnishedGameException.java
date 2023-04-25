package br.com.ada.apostaapi.exceptions;

public class FnishedGameException extends RuntimeException {
    public FnishedGameException(String message) {
        super(message);
    }
}
