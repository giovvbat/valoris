package br.ufrn.imd.valoris.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContaModel {
    @NotBlank
    private String numero;
    @NotNull
    private Double saldo;
}
