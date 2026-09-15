package br.com.fiap.campusgigs.servico;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    public List<ServicoResponse> listar() {
        return servicoService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoResponse publicar(@RequestBody @Valid ServicoRequest request, Authentication authentication) {
        return servicoService.publicar(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public ServicoResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ServicoRequest request,
            Authentication authentication
    ) {
        return servicoService.atualizar(id, request, authentication.getName());
    }

    @PatchMapping("/{id}/encerrar")
    public ServicoResponse encerrar(@PathVariable Long id, Authentication authentication) {
        return servicoService.encerrar(id, authentication.getName());
    }
}
