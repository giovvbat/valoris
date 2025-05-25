package br.ufrn.imd.valoris.model;

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
}
