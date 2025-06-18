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
        String numberConta = "123";
        Double balanceConta = 100.0;
        ContaDTO contaDTO = new ContaDTO(numberConta, TipoConta.PADRAO,balanceConta);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.empty());
        when(contaDao.saveConta(any(ContaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaModel result = contaService.cadastrarConta(contaDTO);

        assertEquals(contaDTO.number(), result.getNumber());
        assertEquals(contaDTO.balance(), result.getBalance());
        assertEquals(TipoConta.PADRAO, result.getType());
        assertFalse(result instanceof ContaBonusModel);
        assertFalse(result instanceof ContaPoupancaModel);
        verify(contaDao, times(1)).saveConta(any());
    }

    @Test
    void cadastrarContaBonusQuandoNumeroNaoExiste() {
        String numberConta = "456";
        ContaDTO contaDTO = new ContaDTO(numberConta, TipoConta.BONUS, null);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.empty());
        when(contaDao.saveConta(any(ContaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaModel result = contaService.cadastrarConta(contaDTO);

        assertEquals(contaDTO.number(), result.getNumber());
        assertEquals(0.0, result.getBalance());
        assertEquals(TipoConta.BONUS, result.getType());
        assertInstanceOf(ContaBonusModel.class, result);
        assertEquals(10, ((ContaBonusModel) result).getPontuation());
        verify(contaDao, times(1)).saveConta(any());
    }

    @Test
    void cadastrarContaBonusIgnorandoSaldoInicial() {
        String numberConta = "456";
        Double balanceConta = 100.0;
        ContaDTO contaDTO = new ContaDTO(numberConta, TipoConta.BONUS, balanceConta);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.empty());
        when(contaDao.saveConta(any(ContaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaModel result = contaService.cadastrarConta(contaDTO);

        assertEquals(contaDTO.number(), result.getNumber());
        assertEquals(0.0, result.getBalance());
        assertEquals(TipoConta.BONUS, result.getType());
        assertInstanceOf(ContaBonusModel.class, result);
        assertEquals(10, ((ContaBonusModel) result).getPontuation());
        verify(contaDao, times(1)).saveConta(any());
    }

    @Test
    void cadastrarContaPoupancaQuandoNumeroNaoExiste() {
        String numberConta = "789";
        Double balanceConta = 1000.0;
        ContaDTO contaDTO = new ContaDTO(numberConta, TipoConta.POUPANCA, balanceConta);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.empty());
        when(contaDao.saveConta(any(ContaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaModel result = contaService.cadastrarConta(contaDTO);

        assertEquals(contaDTO.number(), result.getNumber());
        assertEquals(contaDTO.balance(), result.getBalance());
        assertEquals(TipoConta.POUPANCA, result.getType());
        assertInstanceOf(ContaPoupancaModel.class, result);
        verify(contaDao, times(1)).saveConta(any());
    }

    @ParameterizedTest
    @MethodSource("tiposConta")
    void cadastrarContaQuandoNumeroJaExiste(TipoConta type, Double balance) {
        String numberConta = "123";
        ContaDTO contaDTO = new ContaDTO(numberConta, type, balance);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.of(new ContaModel()));

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
        String numberConta = "123";
        ContaDTO contaDTO = new ContaDTO(numberConta, TipoConta.POUPANCA,null);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.empty());

        InitialBalanceMissingException ex = assertThrows(InitialBalanceMissingException.class, () -> {
            contaService.cadastrarConta(contaDTO);
        });

        assertEquals("Saldo inicial obrigatório para contas do tipo poupança.", ex.getMessage());
        verify(contaDao, never()).saveConta(any());
    }

    @Test
    void cadastrarContaPadraoSemSaldoInicial() {
        String numberConta = "123";
        ContaDTO contaDTO = new ContaDTO(numberConta, TipoConta.PADRAO, null);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.empty());

        InitialBalanceMissingException ex = assertThrows(InitialBalanceMissingException.class, () -> {
            contaService.cadastrarConta(contaDTO);
        });

        assertEquals("Saldo inicial obrigatório para contas do tipo padrão.", ex.getMessage());
        verify(contaDao, never()).saveConta(any());
    }

    @Test
    void consultarContaQuandoContaPadraoExiste() {
        String numberConta = "123";
        Double balanceConta = 100.0;
        ContaModel mockedConta = new ContaModel(numberConta, balanceConta, TipoConta.PADRAO);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.of(mockedConta));

        ContaModel result = contaService.findByNumeroIfExists(numberConta);

        assertEquals(mockedConta.getNumber(), result.getNumber());
        assertEquals(mockedConta.getBalance(), result.getBalance());
        assertEquals(TipoConta.PADRAO, result.getType());
        assertFalse(result instanceof ContaBonusModel);
        assertFalse(result instanceof ContaPoupancaModel);
        verify(contaDao, times(1)).findByNumero(numberConta);
    }

    @Test
    void consultarContaQuandoContaPoupancaExiste() {
        String numberConta = "123";
        Double balanceConta = 100.0;
        ContaPoupancaModel mockedConta = new ContaPoupancaModel(numberConta, balanceConta, TipoConta.POUPANCA);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.of(mockedConta));

        ContaModel result = contaService.findByNumeroIfExists(numberConta);

        assertEquals(mockedConta.getNumber(), result.getNumber());
        assertEquals(mockedConta.getBalance(), result.getBalance());
        assertEquals(TipoConta.POUPANCA, result.getType());
        assertInstanceOf(ContaPoupancaModel.class, result);
        verify(contaDao, times(1)).findByNumero(numberConta);
    }

    @Test
    void consultarContaQuandoContaBonusExiste() {
        String numberConta = "123";
        ContaBonusModel mockedConta = new ContaBonusModel(numberConta, null, TipoConta.BONUS, 10);

        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.of(mockedConta));

        ContaModel result = contaService.findByNumeroIfExists(numberConta);

        assertEquals(mockedConta.getNumber(), result.getNumber());
        assertEquals(mockedConta.getBalance(), result.getBalance());
        assertEquals(TipoConta.BONUS, result.getType());
        assertInstanceOf(ContaBonusModel.class, result);
        assertEquals(mockedConta.getPontuation(), ((ContaBonusModel) result).getPontuation());
        verify(contaDao, times(1)).findByNumero(numberConta);
    }

    @Test
    void consultarContaQuandoNaoExiste() {
        String numberConta = "123";
        when(contaDao.findByNumero(numberConta)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            contaService.findByNumeroIfExists(numberConta);
        });

        assertEquals("Conta de número 123 não encontrada.", ex.getMessage());
        verify(contaDao, times(1)).findByNumero(numberConta);
    }
}
