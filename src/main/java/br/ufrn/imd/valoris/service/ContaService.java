package br.ufrn.imd.valoris.service;

import br.ufrn.imd.valoris.dao.ContaDao;
import br.ufrn.imd.valoris.dto.ContaDTO;
import br.ufrn.imd.valoris.dto.RenderJurosDTO;
import br.ufrn.imd.valoris.dto.TransacaoDTO;
import br.ufrn.imd.valoris.dto.TransferenciaDTO;
import br.ufrn.imd.valoris.enums.TipoConta;
import br.ufrn.imd.valoris.exception.InitialBalanceMissingException;
import br.ufrn.imd.valoris.exception.NotEnoughAccountBalanceException;
import br.ufrn.imd.valoris.exception.ResourceAlreadyExistsException;
import br.ufrn.imd.valoris.exception.ResourceNotFoundException;
import br.ufrn.imd.valoris.model.ContaBonusModel;
import br.ufrn.imd.valoris.model.ContaModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.valoris.model.ContaPoupancaModel;
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

        if (contaDTO.tipoConta() == TipoConta.BONUS) {
            return contaDao.saveConta(setarContaBonus(contaDTO));
        }

        if (contaDTO.tipoConta() == TipoConta.POUPANCA) {
            return contaDao.saveConta(setarContaPoupanca(contaDTO));
        }

        return contaDao.saveConta(setarContaPadrao(contaDTO));
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
        incrementarPontuacao(conta, determinarPontosIncrementados(transacaoDTO.valor(), 100));
        return conta;
    }

    public ContaModel transferir(String numeroOrigem, TransferenciaDTO transferenciaDTO) {
        ContaModel contaOrigem = findByNumeroIfExists(numeroOrigem);
        ContaModel contaDestino = findByNumeroIfExists(transferenciaDTO.numeroDestino());
        verificarSaldoSuficiente(contaOrigem, transferenciaDTO.valor());
        contaOrigem.debitar(transferenciaDTO.valor());
        contaDestino.creditar(transferenciaDTO.valor());
        incrementarPontuacao(contaDestino, determinarPontosIncrementados(transferenciaDTO.valor(), 200));

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

    private Integer determinarPontosIncrementados(Double valor, Integer referencia) {
        return BigDecimal.valueOf(valor).divideToIntegralValue(BigDecimal.valueOf(referencia)).intValue();
    }

    private void incrementarPontuacao(ContaModel conta, Integer pontosIncrementados) {
        if (conta instanceof ContaBonusModel contaBonus) {
            contaBonus.setPontuation(contaBonus.getPontuation() + pontosIncrementados);
        }
    }

    public List<ContaModel> renderJuros(RenderJurosDTO renderJurosDTO) {
        List<ContaModel> contas = contaDao.findAll();
        List<ContaModel> contasAtualizadas = new ArrayList<>();

        for(ContaModel conta: contas) {
            if (conta instanceof ContaPoupancaModel contaPoupanca) {
                contaPoupanca.renderJuros(renderJurosDTO.taxa());
                contasAtualizadas.add(conta);
            }
        }
        return contasAtualizadas;
    }

    private ContaModel setarContaBonus(ContaDTO contaDTO) {
        ContaBonusModel contaBonus = new ContaBonusModel();
        contaBonus.setNumero(contaDTO.numero());
        contaBonus.setSaldo(0.0);
        contaBonus.setPontuation(10);

        return contaBonus;
    }

    private ContaModel setarContaPoupanca(ContaDTO contaDTO) {
        if (contaDTO.saldoInicial() == null) {
            throw new InitialBalanceMissingException("Saldo inicial obrigatório para contas do tipo poupança.");
        }

        ContaPoupancaModel contaPoupanca = new ContaPoupancaModel();
        contaPoupanca.setNumero(contaDTO.numero());
        contaPoupanca.setSaldo(contaDTO.saldoInicial());

        return contaPoupanca;
    }

    private ContaModel setarContaPadrao(ContaDTO contaDTO) {
        ContaModel contaPadrao = new ContaModel();
        contaPadrao.setNumero(contaDTO.numero());
        contaPadrao.setSaldo(0.0);

        return contaPadrao;
    }
}
