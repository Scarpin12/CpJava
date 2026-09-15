package br.com.fiap.campusgigs.cep;

import br.com.fiap.campusgigs.exception.CepIndisponivelException;
import br.com.fiap.campusgigs.exception.CepInvalidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final CepService cepService;

    public CepResponse buscarEndereco(String cep) {
        var cepLimpo = cep.replaceAll("\\D", "");
        try {
            var resposta = cepService.buscarPorCep(cepLimpo);
            if (resposta == null || resposta.invalido()) {
                throw new CepInvalidoException("O CEP " + cep + " não foi encontrado");
            }
            return resposta;
        } catch (CepInvalidoException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Falha ao consultar o serviço de CEP para {}", cep, e);
            throw new CepIndisponivelException("Serviço de consulta de CEP indisponível no momento, tente novamente mais tarde");
        }
    }
}
