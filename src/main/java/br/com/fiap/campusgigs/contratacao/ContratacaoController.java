package br.com.fiap.campusgigs.contratacao;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contratacoes")
@RequiredArgsConstructor
public class ContratacaoController {

    private final ContratacaoService contratacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratacaoResponse contratar(@RequestBody @Valid ContratacaoRequest request, Authentication authentication) {
        return contratacaoService.contratar(request, authentication.getName());
    }
}
