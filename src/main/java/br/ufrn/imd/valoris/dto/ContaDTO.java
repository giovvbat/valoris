package br.ufrn.imd.valoris.dto;

import br.ufrn.imd.valoris.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ContaDTO(@NotBlank String number, @NotNull TipoConta type, @PositiveOrZero Double balance) {
}

