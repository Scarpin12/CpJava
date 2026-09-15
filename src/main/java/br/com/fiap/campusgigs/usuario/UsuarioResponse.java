package br.com.fiap.campusgigs.usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cidade,
        String uf,
        Papel papel
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCidade(),
                usuario.getUf(),
                usuario.getPapel()
        );
    }
}
