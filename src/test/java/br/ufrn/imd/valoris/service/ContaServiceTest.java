package br.ufrn.imd.valoris.service;

import br.ufrn.imd.valoris.dao.ContaDao;
import br.ufrn.imd.valoris.dto.ContaDTO;
import br.ufrn.imd.valoris.dto.SaldoDTO;
import br.ufrn.imd.valoris.enums.TipoConta;
import br.ufrn.imd.valoris.exception.InitialBalanceMissingException;
import br.ufrn.imd.valoris.exception.ResourceAlreadyExistsException;
import br.ufrn.imd.valoris.exception.ResourceNotFoundException;
import br.ufrn.imd.valoris.model.ContaBonusModel;
import br.ufrn.imd.valoris.model.ContaModel;
import br.ufrn.imd.valoris.model.ContaPoupancaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContaServiceTest {
    private ContaDao contaDao;
    private ContaService contaService;

    @BeforeEach
    void setUp() {
        contaDao = mock(ContaDao.class);
        contaService = new ContaService(contaDao);
    }

    @Test
    void consultarSaldoQuandoContaExiste() {
        String numberConta = "123";
        Double balanceConta = 100.0;
        ContaModel mockedConta = new ContaModel(numberConta, balanceConta, TipoConta.PADRAO);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.of(mockedConta));
        SaldoDTO result = contaService.consultarSaldo(numberConta);

        assertEquals(numberConta, result.number());
        assertEquals(balanceConta, result.balance());
        verify(contaDao, times(1)).findByNumero(numberConta);
    }

    @Test
    void consultarSaldoQuandoContaNaoExiste() {
        String numberConta = "123";
        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            contaService.consultarSaldo(numberConta);
        });

        assertEquals("Conta de número 123 não encontrada.", ex.getMessage());
        verify(contaDao, times(1)).findByNumero(numberConta);
    }

    @Test
    void cadastrarContaPadraoQuandoNumeroNaoExiste() {
        ContaDTO contaDTO = new ContaDTO("123", TipoConta.PADRAO,500.0);

        when(contaDao.findByNumero("123")).thenReturn(Optional.empty());
        when(contaDao.saveConta(any(ContaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaModel result = contaService.cadastrarConta(contaDTO);

        assertEquals("123", result.getNumber());
        assertEquals(500.0, result.getBalance());
        assertEquals(TipoConta.PADRAO, result.getType());
        assertFalse(result instanceof ContaBonusModel);
        assertFalse(result instanceof ContaPoupancaModel);
        verify(contaDao, times(1)).saveConta(any());
    }

    @Test
    void cadastrarContaBonusQuandoNumeroNaoExiste() {
        ContaDTO contaDTO = new ContaDTO("456", TipoConta.BONUS, null);

        when(contaDao.findByNumero("456")).thenReturn(Optional.empty());
        when(contaDao.saveConta(any(ContaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaModel result = contaService.cadastrarConta(contaDTO);

        assertEquals("456", result.getNumber());
        assertEquals(0.0, result.getBalance());
        assertEquals(TipoConta.BONUS, result.getType());
        assertInstanceOf(ContaBonusModel.class, result);
        assertEquals(10, ((ContaBonusModel) result).getPontuation());
        verify(contaDao, times(1)).saveConta(any());
    }

    @Test
    void cadastrarContaBonusIgnorandoSaldoInicial() {
        ContaDTO contaDTO = new ContaDTO("456", TipoConta.BONUS, 500.0);

        when(contaDao.findByNumero("456")).thenReturn(Optional.empty());
        when(contaDao.saveConta(any(ContaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaModel result = contaService.cadastrarConta(contaDTO);

        assertEquals("456", result.getNumber());
        assertEquals(0.0, result.getBalance());
        assertEquals(TipoConta.BONUS, result.getType());
        assertInstanceOf(ContaBonusModel.class, result);
        assertEquals(10, ((ContaBonusModel) result).getPontuation());
        verify(contaDao, times(1)).saveConta(any());
    }

    @Test
    void cadastrarContaPoupancaQuandoNumeroNaoExiste() {
        ContaDTO contaDTO = new ContaDTO("789", TipoConta.POUPANCA, 1000.0);

        when(contaDao.findByNumero("789")).thenReturn(Optional.empty());
        when(contaDao.saveConta(any(ContaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaModel result = contaService.cadastrarConta(contaDTO);

        assertEquals("789", result.getNumber());
        assertEquals(1000.0, result.getBalance());
        assertEquals(TipoConta.POUPANCA, result.getType());
        assertInstanceOf(ContaPoupancaModel.class, result);
        verify(contaDao, times(1)).saveConta(any());
    }

    @ParameterizedTest
    @MethodSource("tiposConta")
    void cadastrarContaQuandoNumeroJaExiste(TipoConta type, Double balance) {
        ContaDTO contaDTO = new ContaDTO("123", type, balance);

        when(contaDao.findByNumero("123")).thenReturn(Optional.of(new ContaModel()));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> {
            contaService.cadastrarConta(contaDTO);
        });

        assertEquals("Conta de número 123 já existe.", ex.getMessage());
        verify(contaDao, never()).saveConta(any());
    }

    private static Stream<Arguments> tiposConta() {
        return Stream.of(
            Arguments.of(TipoConta.PADRAO, 500.0),
            Arguments.of(TipoConta.BONUS, null),
            Arguments.of(TipoConta.POUPANCA, 1000.0)
        );
    }

    @Test
    void cadastrarContaPoupancaSemSaldoInicial() {
        ContaDTO contaDTO = new ContaDTO("123", TipoConta.POUPANCA,null);

        when(contaDao.findByNumero("123")).thenReturn(Optional.empty());

        InitialBalanceMissingException ex = assertThrows(InitialBalanceMissingException.class, () -> {
            contaService.cadastrarConta(contaDTO);
        });

        assertEquals("Saldo inicial obrigatório para contas do tipo poupança.", ex.getMessage());
        verify(contaDao, never()).saveConta(any());
    }

    @Test
    void cadastrarContaPadraoSemSaldoInicial() {
        ContaDTO contaDTO = new ContaDTO("123", TipoConta.PADRAO, null);

        when(contaDao.findByNumero("123")).thenReturn(Optional.empty());

        InitialBalanceMissingException ex = assertThrows(InitialBalanceMissingException.class, () -> {
            contaService.cadastrarConta(contaDTO);
        });

        assertEquals("Saldo inicial obrigatório para contas do tipo padrão.", ex.getMessage());
        verify(contaDao, never()).saveConta(any());
    }
}
