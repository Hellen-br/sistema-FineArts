package finearts.app;

import finearts.infra.ConexaoFactory;
import finearts.infra.ConfiguracaoBanco;
import finearts.infra.MySqlConexaoFactory;
import java.nio.file.Paths;

/**
 * Ponto de entrada de verificação do núcleo FineArts (sem interface gráfica).
 * Executa os testes de regras de negócio (em memória) e, se o MySQL estiver
 * acessível, os testes de integração com o banco.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("FineArts - núcleo de regras de negócio (Etapa 6)");
        SuiteDeTestes suite = new SuiteDeTestes();

        TestesDeNegocio.executar(suite);

        try {
            ConfiguracaoBanco config = ConfiguracaoBanco.carregar(Paths.get("config", "application.properties"));
            ConexaoFactory conexoes = new MySqlConexaoFactory(config);
            conexoes.abrir().close(); // verifica a disponibilidade antes de rodar os testes
            TestesDeIntegracaoMySql.executar(suite, FabricaDeServicos.comMySql(config));
        } catch (Exception e) {
            suite.titulo("Integração com MySQL");
            System.out.println("  [IGNORADO] banco indisponível: " + e.getMessage());
            System.out.println("  Crie o banco com db/FineArts_schema.sql e configure config/application.properties.");
        }

        suite.resumo();
        System.exit(suite.houveFalhas() ? 1 : 0);
    }
}
