package br.ufrn.imd.valoris.handler;

import br.ufrn.imd.valoris.dto.RespostaApiDTO;
import br.ufrn.imd.valoris.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RespostaApiDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RespostaApiDTO(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
}
