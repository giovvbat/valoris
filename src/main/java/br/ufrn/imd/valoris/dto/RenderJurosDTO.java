package br.ufrn.imd.valoris.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RenderJurosDTO(@Positive @NotNull Double tax) {
}
