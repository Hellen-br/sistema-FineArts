package finearts.app;

import static finearts.app.SuiteDeTestes.afirmar;
import static finearts.app.SuiteDeTestes.igual;

import finearts.domain.Artista;
import finearts.domain.Certificado;
import finearts.domain.Cliente;
import finearts.domain.FiltroObras;
import finearts.domain.ObraDeArte;
import finearts.domain.Pedido;
import finearts.domain.Usuario;
import java.math.BigDecimal;
import java.util.List;

/** Testes somente de leitura sobre os dados iniciais de db/FineArts_schema.sql. */
final class TestesDeIntegracaoMySql {
    private TestesDeIntegracaoMySql() { }

    static void executar(SuiteDeTestes s, FabricaDeServicos f) {
        s.titulo("Integração com MySQL (dados iniciais do script SQL)");

        s.teste("Login lê o hash do banco e devolve o tipo correto de usuário", () -> {
            afirmar(f.autenticacao().autenticar("ana@finearts.com", "123456") instanceof Artista, "Ana deveria ser Artista");
            afirmar(f.autenticacao().autenticar("mariana@finearts.com", "123456") instanceof Cliente, "Mariana deveria ser Cliente");
            igual("ADMINISTRADOR", f.autenticacao().autenticar("admin@finearts.com", "admin123").getTipo().name(), "tipo admin");
        });

        s.teste("Catálogo lista só obras disponíveis (as 2 vendidas ficam de fora)", () -> {
            List<ObraDeArte> disponiveis = f.catalogo().pesquisar(new FiltroObras());
            igual(4, disponiveis.size(), "disponíveis");
            igual(6, f.catalogo().pesquisar(new FiltroObras().incluindoIndisponiveis()).size(), "todas");
        });

        s.teste("Filtros por técnica, termo e faixa de preço", () -> {
            igual(2, f.catalogo().pesquisar(new FiltroObras().tecnica("Acrílica")).size(), "acrílica");
            igual(1, f.catalogo().pesquisar(new FiltroObras().termo("Pantanal")).size(), "termo");
            igual(1, f.catalogo().pesquisar(new FiltroObras().precoMinimo(new BigDecimal("2000"))
                    .precoMaximo(new BigDecimal("3000"))).size(), "faixa");
            igual(0, f.catalogo().pesquisar(new FiltroObras().termo("100%_'; DROP TABLE obras_de_arte;--")).size(),
                    "entrada maliciosa tratada como texto");
        });

        s.teste("Histórico de pedidos e certificados da cliente Mariana", () -> {
            Cliente mariana = (Cliente) f.autenticacao().autenticar("mariana@finearts.com", "123456");
            List<Pedido> pedidos = f.compras().listarPedidos(mariana);
            igual(1, pedidos.size(), "pedidos");
            igual(2, pedidos.get(0).getItens().size(), "itens");
            igual(0, new BigDecimal("4700.00").compareTo(pedidos.get(0).getValorTotal()), "total");
            List<Certificado> certs = f.compras().listarCertificados(mariana);
            igual(2, certs.size(), "certificados");
        });

        s.teste("Resumo do artista calculado a partir do banco", () -> {
            Usuario ana = f.autenticacao().autenticar("ana@finearts.com", "123456");
            igual(3, f.catalogo().resumoDoArtista(ana.getId()).getTotal(), "total");
            igual(2, f.catalogo().resumoDoArtista(ana.getId()).getVendidas(), "vendidas");
        });
    }
}
