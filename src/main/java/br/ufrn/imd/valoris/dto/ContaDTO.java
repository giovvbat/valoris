package br.ufrn.imd.valoris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ContaDTO(@NotBlank String numero, @NotNull @PositiveOrZero Double saldoInicial) {
}
