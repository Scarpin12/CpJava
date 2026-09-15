package br.com.fiap.campusgigs.cep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CepResponse(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro
) {

    public boolean invalido() {
        return Boolean.TRUE.equals(erro) || localidade == null;
    }
}
