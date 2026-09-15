package br.com.fiap.campusgigs;

import br.com.fiap.campusgigs.cep.CepService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication
@ImportHttpServices(CepService.class)
public class CampusGigsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusGigsApplication.class, args);
    }

}
