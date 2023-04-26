package br.com.ada.apostaapi.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {


    @ExceptionHandler(FnishedGameException.class)
    public ResponseEntity<Object> handleFnishedGameException(FnishedGameException ex) {
        String errorMessage = ex.getMessage();
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(errorMessage, httpStatus);
    }

    @ExceptionHandler(InvalidTeamException.class)
    public ResponseEntity<Object> handleInvalidTeamException(InvalidTeamException ex) {
        String errorMessage = ex.getMessage();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(errorMessage, httpStatus);
    }
    @ExceptionHandler(UnauthorizedBalanceTransactionException.class)
    public ResponseEntity<Object> handleUnauthorizedBalanceTransactionException(UnauthorizedBalanceTransactionException ex) {
        String errorMessage = ex.getMessage();
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(errorMessage, httpStatus);
    }

    @ExceptionHandler(BetNotFoundException.class)
    public ResponseEntity<Object> handleBetNotFoundException(BetNotFoundException ex) {
        String errorMessage = ex.getMessage();
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(errorMessage, httpStatus);
    }
}
