package br.com.fiap.campusgigs.cep;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(
        url = "https://viacep.com.br/ws",
        accept = "application/json"
)
public interface CepService {

    @GetExchange("/{cep}/json/")
    CepResponse buscarPorCep(@PathVariable String cep);
}
