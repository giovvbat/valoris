package br.ufrn.imd.valoris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

public record RespostaApiDTO(@NotNull HttpStatus status, @NotBlank String mensagem) {
}
