package br.ufrn.imd.valoris.dto;

import jakarta.validation.constraints.NotBlank;

public record ContaDTO(@NotBlank String numero) {
}
