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
    public static final String METHOD_ARGUMENT_NOT_VALID_ERROR_MESSAGE = "Campo inválido: '%s'. Causa: '%s'.";


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


//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
//            WebRequest request) {
//        String errorMessage = getErrorMessages(ex.getBindingResult());
//        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
//        return new ResponseEntity<>(errorMessage, httpStatus);
//    }

    private String getErrorMessages(BindingResult bindingResult) {
        return Stream.concat(
                bindingResult.getFieldErrors().stream().map(this::getMethodArgumentNotValidErrorMessage),
                bindingResult.getGlobalErrors().stream().map(this::getMethodArgumentNotValidErrorMessage)
        ).collect(Collectors.joining(", "));
    }

    private String getMethodArgumentNotValidErrorMessage(FieldError error) {
        return String.format(METHOD_ARGUMENT_NOT_VALID_ERROR_MESSAGE, error.getField(), error.getDefaultMessage());
    }

    private String getMethodArgumentNotValidErrorMessage(ObjectError error) {
        return String.format(METHOD_ARGUMENT_NOT_VALID_ERROR_MESSAGE, error.getObjectName(), error.getDefaultMessage());
    }


}
