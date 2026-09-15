package br.com.fiap.campusgigs.auth;

import br.com.fiap.campusgigs.exception.NegocioException;
import br.com.fiap.campusgigs.usuario.Papel;
import br.com.fiap.campusgigs.usuario.Usuario;
import br.com.fiap.campusgigs.usuario.UsuarioRepository;
import br.com.fiap.campusgigs.usuario.UsuarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new NegocioException("Já existe um usuário cadastrado com esse e-mail");
        }

        var usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .cep(request.cep())
                .papel(Papel.USER)
                .build();

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    public UsuarioResponse autenticar(LoginRequest request) {
        var usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        return UsuarioResponse.from(usuario);
    }
}
