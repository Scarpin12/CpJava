package br.com.fiap.campusgigs.servico;

import java.math.BigDecimal;

public record ServicoResponse(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        BigDecimal preco,
        SituacaoServico situacao,
        String prestadorNome
) {

    public static ServicoResponse from(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getTitulo(),
                servico.getDescricao(),
                servico.getCategoria(),
                servico.getPreco(),
                servico.getSituacao(),
                servico.getPrestador().getNome()
        );
    }
}
