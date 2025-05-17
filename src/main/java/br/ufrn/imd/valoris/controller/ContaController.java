package br.ufrn.imd.valoris.controller;

import br.ufrn.imd.valoris.dto.ContaDTO;
import br.ufrn.imd.valoris.dto.TransacaoDTO;
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

    @PutMapping("/{numero}/debitar")
    public ContaModel debitarConta(@PathVariable String numero, @RequestBody @Valid TransacaoDTO transacaoDTO) {
        return contaService.debitarConta(numero, transacaoDTO);
    }
}
