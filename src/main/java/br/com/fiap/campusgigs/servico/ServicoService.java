package br.com.fiap.campusgigs.servico;

import br.com.fiap.campusgigs.exception.AcessoNegadoException;
import br.com.fiap.campusgigs.exception.RecursoNaoEncontradoException;
import br.com.fiap.campusgigs.usuario.Papel;
import br.com.fiap.campusgigs.usuario.Usuario;
import br.com.fiap.campusgigs.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final UsuarioRepository usuarioRepository;

    public ServicoResponse publicar(ServicoRequest request, String emailUsuarioLogado) {
        var prestador = buscarUsuarioLogado(emailUsuarioLogado);

        var servico = Servico.builder()
                .prestador(prestador)
                .titulo(request.titulo())
                .descricao(request.descricao())
                .categoria(request.categoria())
                .preco(request.preco())
                .situacao(SituacaoServico.ATIVO)
                .build();

        return ServicoResponse.from(servicoRepository.save(servico));
    }

    public List<ServicoResponse> listar() {
        return servicoRepository.findAll().stream()
                .map(ServicoResponse::from)
                .toList();
    }

    public ServicoResponse atualizar(Long id, ServicoRequest request, String emailUsuarioLogado) {
        var servico = buscarServico(id);
        var usuarioLogado = buscarUsuarioLogado(emailUsuarioLogado);

        if (!servico.getPrestador().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Você só pode editar os próprios serviços");
        }

        servico.setTitulo(request.titulo());
        servico.setDescricao(request.descricao());
        servico.setCategoria(request.categoria());
        servico.setPreco(request.preco());

        return ServicoResponse.from(servicoRepository.save(servico));
    }

    public ServicoResponse encerrar(Long id, String emailUsuarioLogado) {
        var servico = buscarServico(id);
        var usuarioLogado = buscarUsuarioLogado(emailUsuarioLogado);

        var mesmoDono = servico.getPrestador().getId().equals(usuarioLogado.getId());
        var admin = usuarioLogado.getPapel() == Papel.ADMIN;

        if (!mesmoDono && !admin) {
            throw new AcessoNegadoException("Você só pode encerrar os próprios serviços");
        }

        servico.setSituacao(SituacaoServico.ENCERRADO);
        return ServicoResponse.from(servicoRepository.save(servico));
    }

    public Servico buscarServico(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado"));
    }

    private Usuario buscarUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));
    }
}
