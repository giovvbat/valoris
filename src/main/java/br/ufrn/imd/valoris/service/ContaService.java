package br.ufrn.imd.valoris.service;

import br.ufrn.imd.valoris.dao.ContaDao;
import br.ufrn.imd.valoris.dto.ContaDTO;
import br.ufrn.imd.valoris.dto.TransacaoDTO;
import br.ufrn.imd.valoris.dto.TransferenciaDTO;
import br.ufrn.imd.valoris.exception.ResourceNotFoundException;
import br.ufrn.imd.valoris.model.ContaModel;
import jakarta.validation.Valid;
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

    public ContaModel debitarConta(String numero, TransacaoDTO transacaoDTO) {
        ContaModel conta = findByNumeroIfExists(numero);
        conta.debitar(transacaoDTO.valor());
        return conta;
    }

    public ContaModel creditarConta(String numero, TransacaoDTO transacaoDTO) {
        ContaModel conta = findByNumeroIfExists(numero);
        conta.creditar(transacaoDTO.valor());
        return conta;
    }

    public ContaModel transferir(String numeroOrigem, TransferenciaDTO transferenciaDTO) {
        ContaModel contaOrigem = findByNumeroIfExists(numeroOrigem);
        ContaModel contaDestino = findByNumeroIfExists(transferenciaDTO.numeroDestino());
        contaOrigem.debitar(transferenciaDTO.valor());
        contaDestino.creditar(transferenciaDTO.valor());
        return contaOrigem;
    }

    private ContaModel findByNumeroIfExists(String numero) {
        return contaDao.findByNumero(numero).orElseThrow(() -> new ResourceNotFoundException(String.format("Conta de numero {} nao encontrada.")));
    }
}
