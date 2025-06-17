package br.ufrn.imd.valoris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaldoDTO(@NotBlank String number, @NotNull Double balance) {
}
