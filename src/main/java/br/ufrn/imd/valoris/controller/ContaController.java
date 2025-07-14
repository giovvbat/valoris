package br.ufrn.imd.valoris.controller;

import br.ufrn.imd.valoris.dto.*;
import br.ufrn.imd.valoris.model.ContaModel;
import br.ufrn.imd.valoris.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banco/conta")
public class ContaController {
    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    public ContaModel cadastrarConta(@RequestBody @Valid ContaDTO contaDTO) {
        return contaService.cadastrarConta(contaDTO)
    }

    @GetMapping("/{id}")
    public ContaModel consultarConta(@PathVariable String id) {
        return contaService.findByNumeroIfExists(id);
    }

    @GetMapping("/{id}/saldo")
    public SaldoDTO consultarSaldo(@PathVariable String id) {
        return contaService.consultarSaldo(id);
    }

    @GetMapping
    public List<ContaModel> listarContas() {
        return contaService.findAll();
    }

    @PutMapping("/{id}/debito")
    public ContaModel debitarConta(@PathVariable String id, @RequestBody @Valid TransacaoDTO transacaoDTO) {
        return contaService.debitarConta(id, transacaoDTO);
    }

    @PutMapping("/{id}/credito")
    public ContaModel creditarConta(@PathVariable String id, @RequestBody @Valid TransacaoDTO transacaoDTO) {
        return contaService.creditarConta(id, transacaoDTO);
    }

    @PutMapping("/transferencia")
    public List<ContaModel> transferir(@RequestBody @Valid TransferenciaDTO transferenciaDTO) {
        return contaService.transferir(transferenciaDTO);
    }

    @PutMapping("/rendimento")
    public List<ContaModel> renderJuros(@RequestBody @Valid RenderJurosDTO renderJurosDTO) {
        return contaService.renderJuros(renderJurosDTO);
    }
}
