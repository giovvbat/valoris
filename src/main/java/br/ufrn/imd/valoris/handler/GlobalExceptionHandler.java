package br.ufrn.imd.valoris.handler;

import br.ufrn.imd.valoris.dto.RespostaApiDTO;
import br.ufrn.imd.valoris.exception.OperacaoInvalidaException;
import br.ufrn.imd.valoris.exception.ResourceAlreadyExistsException;
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

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<RespostaApiDTO> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new RespostaApiDTO(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(OperacaoInvalidaException.class)
    public ResponseEntity<RespostaApiDTO> handleOperacaoInvalidaException(OperacaoInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RespostaApiDTO(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }
}
