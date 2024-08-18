package br.com.marco.screenmatchspring.principal;

import br.com.marco.screenmatchspring.model.DadosEpisodio;
import br.com.marco.screenmatchspring.model.DadosSerie;
import br.com.marco.screenmatchspring.model.DadosTemporada;
import br.com.marco.screenmatchspring.model.Episodio;
import br.com.marco.screenmatchspring.service.ConsumoApi;
import br.com.marco.screenmatchspring.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner scanner = new Scanner(System.in);

    private final String ENDERECO = "https://www.omdbapi.com?t=";
    private final String API_KEY = "&apikey=becbcd51";

    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados converteDados = new ConverteDados();

    public void exibeMenu() {
        System.out.print("Digite o nome de uma serie:: ");
        var nomeSerie = scanner.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(' ', '+') + API_KEY);
        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
//        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumoApi.obterDados(
                    ENDERECO + nomeSerie.replace(' ', '+') + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

//        temporadas.forEach(System.out::println);
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

//        System.out.println("Top 10 episodios");
//        dadosEpisodios
//                .stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro(N/A):: " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenacao" + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite:: " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapper:: " + e))
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

//        System.out.println("Digite um titulo: ");
//        var trechoTitulo = scanner.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()) {
//            System.out.println("TEmporada: " + episodioBuscado.get().getTemporada());
//        } else {
//            System.out.println("Nao encontado");
//        }
//
//        System.out.println("a partir de que ano voce deseja ver os episodios?");
//        var ano = scanner.nextInt();
//        scanner.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano,1,1);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() !=  null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada:: " + e.getTemporada() +
//                        " Episodio:: " + e.getTitulo() +
//                        " Data de Lancamento:: " + e.getDataLancamento().format(formatter)
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                                Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println(
                "media: " + est.getAverage() + "\n" +
                "Melhor episodio: " + est.getMax() + "\n" +
                "pior episodio: " + est.getMin() + "\n" +
                "episodios avaliados: " + est.getCount()
        );

    }
}
