package br.com.fiap.campusgigs.servico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ServicoRequest(
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotBlank String categoria,
        @NotNull @Positive BigDecimal preco
) {
}
