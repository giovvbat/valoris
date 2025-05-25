package br.ufrn.imd.valoris.service;

import br.ufrn.imd.valoris.dao.ContaDao;
import br.ufrn.imd.valoris.dto.ContaDTO;
import br.ufrn.imd.valoris.dto.TransacaoDTO;
import br.ufrn.imd.valoris.dto.TransferenciaDTO;
import br.ufrn.imd.valoris.exception.NotEnoughAccountBalanceException;
import br.ufrn.imd.valoris.exception.ResourceAlreadyExistsException;
import br.ufrn.imd.valoris.exception.ResourceNotFoundException;
import br.ufrn.imd.valoris.model.ContaModel;
import org.springframework.stereotype.Service;

@Service
public class ContaService {
    private final ContaDao contaDao;

    public ContaService(ContaDao contaDao) {
        this.contaDao = contaDao;
    }

    public ContaModel cadastrarConta(ContaDTO contaDTO) {
        if (contaDao.findByNumero(contaDTO.numero()).isPresent()) {
            throw new ResourceAlreadyExistsException(String.format("Conta de número %s já existe.", contaDTO.numero()));
        }

        ContaModel conta = new ContaModel();
        conta.setNumero(contaDTO.numero());
        conta.setSaldo(contaDTO.saldoInicial());

        return contaDao.saveConta(conta);
    }

    public Double consultarSaldo(String numero) {
        ContaModel conta = findByNumeroIfExists(numero);
        return conta.getSaldo();
    }

    public ContaModel debitarConta(String numero, TransacaoDTO transacaoDTO) {
        ContaModel conta = findByNumeroIfExists(numero);
        verificarSaldoSuficiente(conta, transacaoDTO.valor());
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
        verificarSaldoSuficiente(contaOrigem, transferenciaDTO.valor());
        contaOrigem.debitar(transferenciaDTO.valor());
        contaDestino.creditar(transferenciaDTO.valor());
        return contaOrigem;
    }

    private ContaModel findByNumeroIfExists(String numero) {
        return contaDao.findByNumero(numero).orElseThrow(() -> new ResourceNotFoundException(String.format("Conta de número %s não encontrada.", numero)));
    }

    private void verificarSaldoSuficiente(ContaModel conta, Double valorRequerido) {
        if (conta.getSaldo() < valorRequerido) {
            throw new NotEnoughAccountBalanceException(String.format("Saldo da conta %s insuficiente.", conta.getNumero()));
        }
    }
}
