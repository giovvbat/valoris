package br.ufrn.imd.valoris.model;

import br.ufrn.imd.valoris.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaModel {
    @NotBlank
    protected String number;
    @NotNull
    protected Double balance;
    @NotNull
    protected TipoConta type;

    public void debitar(@PositiveOrZero @NotNull Double valor) {
        this.balance = this.balance - valor;
    }

    public void creditar(@PositiveOrZero @NotNull Double valor) {
        this.balance = this.balance + valor;
    }
}
