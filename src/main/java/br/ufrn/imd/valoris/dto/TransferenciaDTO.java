package br.ufrn.imd.valoris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferenciaDTO(@NotBlank String numeroDestino, @NotNull Double valor) {
}
