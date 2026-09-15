package br.com.fiap.campusgigs.contratacao;

import jakarta.validation.constraints.NotNull;

public record ContratacaoRequest(
        @NotNull Long servicoId
) {
}
