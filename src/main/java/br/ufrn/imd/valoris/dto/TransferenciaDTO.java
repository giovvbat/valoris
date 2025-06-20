package br.ufrn.imd.valoris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TransferenciaDTO(@NotBlank String from, @NotBlank String to, @PositiveOrZero @NotNull Double amount) {
}
