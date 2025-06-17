package br.ufrn.imd.valoris.service;

import br.ufrn.imd.valoris.dao.ContaDao;
import br.ufrn.imd.valoris.dto.SaldoDTO;
import br.ufrn.imd.valoris.enums.TipoConta;
import br.ufrn.imd.valoris.exception.ResourceNotFoundException;
import br.ufrn.imd.valoris.model.ContaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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
}
