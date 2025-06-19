package br.ufrn.imd.valoris.model;

import br.ufrn.imd.valoris.enums.TipoConta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContaBonusModel extends ContaModel {
    @PositiveOrZero
    @NotNull
    private Integer pontuation;

    public ContaBonusModel() {}

    public ContaBonusModel(String numberConta, Double balanceConta, TipoConta tipoConta, Integer pontuation) {
        super(numberConta, balanceConta, tipoConta);
        this.pontuation = pontuation;
    }

}
