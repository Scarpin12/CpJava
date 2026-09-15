package br.com.fiap.campusgigs.contratacao;

import java.time.LocalDateTime;

public record ContratacaoResponse(
        Long id,
        Long servicoId,
        String servicoTitulo,
        String contratanteNome,
        SituacaoContratacao situacao,
        LocalDateTime dataContratacao
) {

    public static ContratacaoResponse from(Contratacao contratacao) {
        return new ContratacaoResponse(
                contratacao.getId(),
                contratacao.getServico().getId(),
                contratacao.getServico().getTitulo(),
                contratacao.getContratante().getNome(),
                contratacao.getSituacao(),
                contratacao.getDataContratacao()
        );
    }
}
