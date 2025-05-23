package br.ufrn.imd.valoris.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ContaModel {
    @NotBlank
    protected String numero;
    @NotNull
    protected Double saldo;

    public void debitar(@PositiveOrZero @NotNull Double valor) {
        this.saldo = this.saldo - valor;
    }

    public void creditar(@PositiveOrZero @NotNull Double valor) {
        this.saldo = this.saldo + valor;
    }
}
