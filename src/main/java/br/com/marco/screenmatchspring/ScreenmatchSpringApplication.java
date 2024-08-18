package br.com.marco.screenmatchspring;

import br.com.marco.screenmatchspring.model.DadosSerie;
import br.com.marco.screenmatchspring.service.ConsumoApi;
import br.com.marco.screenmatchspring.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchSpringApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ScreenmatchSpringApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var search = "gilmore girls";
        String baseUrl = "https://www.omdbapi.com?t="
                + search.replace(' ', '+')
                + "&apikey=becbcd51";

        System.out.println(baseUrl);

        var consumoApi = new ConsumoApi();
        var json = consumoApi.obterDados(baseUrl);
        System.out.println(json);
        ConverteDados converteDados = new ConverteDados();
        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dados);
    }
}
