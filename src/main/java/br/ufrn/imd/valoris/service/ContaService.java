package br.ufrn.imd.valoris.service;

import br.ufrn.imd.valoris.dao.ContaDao;
import br.ufrn.imd.valoris.dto.*;
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
        if (contaDao.findByNumero(contaDTO.number()).isPresent()) {
            throw new ResourceAlreadyExistsException(String.format("Conta de número %s já existe.", contaDTO.number()));
        }

        if (contaDTO.type() == TipoConta.BONUS) {
            return contaDao.saveConta(setarContaBonus(contaDTO));
        }

        if (contaDTO.type() == TipoConta.POUPANCA) {
            return contaDao.saveConta(setarContaPoupanca(contaDTO));
        }

        return contaDao.saveConta(setarContaPadrao(contaDTO));
    }

    public SaldoDTO consultarSaldo(String numero) {
        ContaModel conta = findByNumeroIfExists(numero);
        return new SaldoDTO(conta.getNumero(), conta.getSaldo());
    }

    public ContaModel debitarConta(String numero, TransacaoDTO transacaoDTO) {
        ContaModel conta = findByNumeroIfExists(numero);
        verificarSaldoSuficiente(conta, transacaoDTO.amount());
        conta.debitar(transacaoDTO.amount());
        return conta;
    }

    public ContaModel creditarConta(String numero, TransacaoDTO transacaoDTO) {
        ContaModel conta = findByNumeroIfExists(numero);
        conta.creditar(transacaoDTO.amount());
        incrementarPontuacao(conta, determinarPontosIncrementados(transacaoDTO.amount(), 100));
        return conta;
    }

    public List<ContaModel> transferir(TransferenciaDTO transferenciaDTO) {
        ContaModel contaOrigem = findByNumeroIfExists(transferenciaDTO.from());
        ContaModel contaDestino = findByNumeroIfExists(transferenciaDTO.to());
        verificarSaldoSuficiente(contaOrigem, transferenciaDTO.amount());
        contaOrigem.debitar(transferenciaDTO.amount());
        contaDestino.creditar(transferenciaDTO.amount());
        incrementarPontuacao(contaDestino, determinarPontosIncrementados(transferenciaDTO.amount(), 150));

        return List.of(contaOrigem, contaDestino);
    }

    private ContaModel findByNumeroIfExists(String numero) {
        return contaDao.findByNumero(numero).orElseThrow(() -> new ResourceNotFoundException(String.format("Conta de número %s não encontrada.", numero)));
    }

    private void verificarSaldoSuficiente(ContaModel conta, Double valorRequerido) {
        Double novoSaldo = conta.getSaldo() - valorRequerido;
        Double limite = (conta instanceof ContaPoupancaModel) ? 0.0 : -1000.0;

        if (novoSaldo < limite) {
            throw new NotEnoughAccountBalanceException(
                    String.format(
                            "Saldo da conta %s insuficiente. O limite mínimo permitido do saldo é de R$ %.2f.",
                            conta.getNumero(),
                            limite
                    )
            );
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
                contaPoupanca.renderJuros(renderJurosDTO.tax());
                contasAtualizadas.add(conta);
            }
        }

        return contasAtualizadas;
    }

    private ContaModel setarContaBonus(ContaDTO contaDTO) {
        ContaBonusModel contaBonus = new ContaBonusModel();
        contaBonus.setNumero(contaDTO.number());
        contaBonus.setSaldo(0.0);
        contaBonus.setPontuation(10);

        return contaBonus;
    }

    private ContaModel setarContaPoupanca(ContaDTO contaDTO) {
        if (contaDTO.balance() == null) {
            throw new InitialBalanceMissingException("Saldo inicial obrigatório para contas do tipo poupança.");
        }

        ContaPoupancaModel contaPoupanca = new ContaPoupancaModel();
        contaPoupanca.setNumero(contaDTO.number());
        contaPoupanca.setSaldo(contaDTO.balance());

        return contaPoupanca;
    }

    private ContaModel setarContaPadrao(ContaDTO contaDTO) {
        if (contaDTO.balance() == null) {
            throw new InitialBalanceMissingException("Saldo inicial obrigatório para contas do tipo padrão.");
        }

        ContaModel contaPadrao = new ContaModel();
        contaPadrao.setNumero(contaDTO.number());
        contaPadrao.setSaldo(contaDTO.balance());

        return contaPadrao;
    }
}
