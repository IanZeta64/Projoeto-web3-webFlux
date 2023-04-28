package br.com.ada.apostaapi.exceptions;

public class MicrosservicesConnectionException extends RuntimeException{
    public MicrosservicesConnectionException(String message) {
        super(message);
    }
}
