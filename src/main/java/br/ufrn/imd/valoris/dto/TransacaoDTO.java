package br.ufrn.imd.valoris.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TransacaoDTO(@PositiveOrZero @NotNull Double valor) {
}
