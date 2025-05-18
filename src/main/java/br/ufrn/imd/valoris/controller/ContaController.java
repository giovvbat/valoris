package br.ufrn.imd.valoris.controller;

import br.ufrn.imd.valoris.dto.ContaDTO;
import br.ufrn.imd.valoris.dto.TransacaoDTO;
import br.ufrn.imd.valoris.dto.TransferenciaDTO;
import br.ufrn.imd.valoris.model.ContaModel;
import br.ufrn.imd.valoris.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contas")
public class ContaController {
    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    public ContaModel cadastrarConta(@RequestBody @Valid ContaDTO contaDTO) {
        return contaService.cadastrarConta(contaDTO);
    }

    @GetMapping("/{numero}/saldo")
    public Double consultarSaldo(@PathVariable String numero) {
        return contaService.consultarSaldo(numero);
    }

    @PutMapping("/{numero}/debitar")
    public ContaModel debitarConta(@PathVariable String numero, @RequestBody @Valid TransacaoDTO transacaoDTO) {
        return contaService.debitarConta(numero, transacaoDTO);
    }

    @PutMapping("/{numero}/creditar")
    public ContaModel creditarConta(@PathVariable String numero, @RequestBody @Valid TransacaoDTO transacaoDTO) {
        return contaService.creditarConta(numero, transacaoDTO);
    }

    @PutMapping("/{numero}/transferir")
    public ContaModel transferir(@PathVariable("numero") String numeroOrigem, @RequestBody @Valid TransferenciaDTO transferenciaDTO) {
        return contaService.transferir(numeroOrigem, transferenciaDTO);
    }
}
