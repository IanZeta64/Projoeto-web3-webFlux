package br.com.ada.apostaapi.exceptions;

public class SaveBetException extends RuntimeException{
    public SaveBetException(String message){
        super(message);
    }
}
