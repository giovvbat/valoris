package br.ufrn.imd.valoris.dao;

import br.ufrn.imd.valoris.model.ContaModel;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ContaDao {
    private final Map<String, ContaModel> contas = new HashMap<>();

    public ContaModel saveConta(ContaModel contaModel) {
        contas.put(contaModel.getNumero(), contaModel);
        return contaModel;
    }

    public Optional<ContaModel> findByNumero(String numero) {
        if (contas.containsKey(numero)) {
            return Optional.of(contas.get(numero));
        }

        return Optional.empty();
    }
}
