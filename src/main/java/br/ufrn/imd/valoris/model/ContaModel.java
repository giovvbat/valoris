package br.ufrn.imd.valoris.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ContaModel {
    @NotBlank
    private String numero;
    @NotNull
    private Double saldo;

    public void debitar(@NotNull Double valor) {
        this.saldo = this.saldo - valor;
    }

    public void creditar(@NotNull Double valor) {
        this.saldo = this.saldo + valor;
    }
}
