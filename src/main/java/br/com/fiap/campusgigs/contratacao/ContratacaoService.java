package br.com.fiap.campusgigs.contratacao;

import br.com.fiap.campusgigs.exception.NegocioException;
import br.com.fiap.campusgigs.exception.RecursoNaoEncontradoException;
import br.com.fiap.campusgigs.servico.ServicoService;
import br.com.fiap.campusgigs.servico.SituacaoServico;
import br.com.fiap.campusgigs.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;
    private final ServicoService servicoService;
    private final UsuarioRepository usuarioRepository;

    public ContratacaoResponse contratar(ContratacaoRequest request, String emailUsuarioLogado) {
        var servico = servicoService.buscarServico(request.servicoId());
        var contratante = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));

        if (servico.getSituacao() != SituacaoServico.ATIVO) {
            throw new NegocioException("Esse serviço não está ativo e não pode ser contratado");
        }

        if (servico.getPrestador().getId().equals(contratante.getId())) {
            throw new NegocioException("Você não pode contratar o próprio serviço");
        }

        var contratacao = Contratacao.builder()
                .servico(servico)
                .contratante(contratante)
                .situacao(SituacaoContratacao.SOLICITADA)
                .dataContratacao(LocalDateTime.now())
                .build();

        return ContratacaoResponse.from(contratacaoRepository.save(contratacao));
    }
}
