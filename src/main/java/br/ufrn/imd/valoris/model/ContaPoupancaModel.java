package br.ufrn.imd.valoris.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContaPoupancaModel extends ContaModel {

    public void renderJuros(Double taxa) {
        var saldo = super.getSaldo();
        var rendimento = saldo * (taxa/100);
        super.creditar(rendimento);
    }
}
