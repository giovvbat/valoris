package br.ufrn.imd.valoris.controller;

import br.ufrn.imd.valoris.dto.ContaDTO;
import br.ufrn.imd.valoris.model.ContaModel;
import br.ufrn.imd.valoris.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
