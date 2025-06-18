package br.ufrn.imd.valoris.model;

import br.ufrn.imd.valoris.enums.TipoConta;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContaPoupancaModel extends ContaModel {

    public ContaPoupancaModel() {}

    public ContaPoupancaModel(String numberConta, Double balanceConta, TipoConta tipoConta) {
        super(numberConta, balanceConta, tipoConta);
    }

    public void renderJuros(Double taxa) {
        Double saldo = super.getBalance();
        Double rendimento = saldo * (taxa/100);
        super.creditar(rendimento);
    }
}
