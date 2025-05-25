package br.ufrn.imd.valoris.handler;

import br.ufrn.imd.valoris.dto.RespostaApiDTO;
import br.ufrn.imd.valoris.exception.InitialBalanceMissingException;
import br.ufrn.imd.valoris.exception.NotEnoughAccountBalanceException;
import br.ufrn.imd.valoris.exception.ResourceAlreadyExistsException;
import br.ufrn.imd.valoris.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RespostaApiDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RespostaApiDTO(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<RespostaApiDTO> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new RespostaApiDTO(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(NotEnoughAccountBalanceException.class)
    public ResponseEntity<RespostaApiDTO> handleNotEnoughAccountBalanceException(NotEnoughAccountBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RespostaApiDTO(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(InitialBalanceMissingException.class)
    public ResponseEntity<RespostaApiDTO> handleInitialBalanceMissingException(InitialBalanceMissingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RespostaApiDTO(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.toString());
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
