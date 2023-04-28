package br.com.ada.apostaapi.exceptions;

public class GameNotFoundException extends RuntimeException{
    public GameNotFoundException(String message){
        super(message);
    }
}
