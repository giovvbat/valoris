package br.ufrn.imd.valoris.dto;

import br.ufrn.imd.valoris.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContaDTO(@NotBlank String numero, @NotNull TipoConta tipoConta) {
}
