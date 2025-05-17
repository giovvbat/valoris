package br.ufrn.imd.valoris.service;

import br.ufrn.imd.valoris.dao.ContaDao;
import br.ufrn.imd.valoris.dto.ContaDTO;
import br.ufrn.imd.valoris.model.ContaModel;
import org.springframework.stereotype.Service;

@Service
public class ContaService {
    private final ContaDao contaDao;

    public ContaService(ContaDao contaDao) {
        this.contaDao = contaDao;
    }

    public ContaModel cadastrarConta(ContaDTO contaDTO) {
        ContaModel conta = new ContaModel();
        conta.setNumero(contaDTO.numero());
        conta.setSaldo(0.0);

        return contaDao.saveConta(conta);
    }
}
