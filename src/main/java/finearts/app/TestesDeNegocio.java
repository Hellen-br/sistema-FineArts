package finearts.app;

import static finearts.app.SuiteDeTestes.afirmar;
import static finearts.app.SuiteDeTestes.igual;
import static finearts.app.SuiteDeTestes.lanca;

import finearts.domain.Administrador;
import finearts.domain.Artista;
import finearts.domain.Carrinho;
import finearts.domain.Cliente;
import finearts.domain.Cpf;
import finearts.domain.FiltroObras;
import finearts.domain.FormaPagamento;
import finearts.domain.ObraDeArte;
import finearts.domain.Pedido;
import finearts.domain.StatusPedido;
import finearts.domain.Usuario;
import finearts.domain.excecao.AcessoNegadoException;
import finearts.domain.excecao.CredenciaisInvalidasException;
import finearts.domain.excecao.NegocioException;
import finearts.domain.excecao.ObraIndisponivelException;
import finearts.domain.excecao.ValidacaoException;
import finearts.seguranca.Pbkdf2PasswordEncoder;
import finearts.service.DadosCadastro;
import finearts.service.DadosObra;
import finearts.service.ResumoArtista;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Testes das regras de negócio com repositórios em memória (não precisam de banco). */
final class TestesDeNegocio {
    private static final String CPF_A = "529.982.247-25";
    private static final String CPF_B = "168.995.350-09";
    private static final String CPF_C = "111.444.777-35";

    private TestesDeNegocio() { }

    static void executar(SuiteDeTestes s) {
        s.titulo("Regras de negócio (repositórios em memória)");

        s.teste("CPF: aceita válido e rejeita inválido/repetido", () -> {
            afirmar(Cpf.valido(CPF_A), "CPF válido rejeitado");
            afirmar(!Cpf.valido("111.111.111-11"), "CPF repetido aceito");
            afirmar(!Cpf.valido("123.456.789-00"), "CPF com dígito errado aceito");
            igual("529.982.247-25", Cpf.formatar("52998224725"), "formatação");
        });

        s.teste("Senha: PBKDF2 confere a correta e rejeita errada/corrompida", () -> {
            Pbkdf2PasswordEncoder enc = new Pbkdf2PasswordEncoder(1000);
            String hash = enc.codificar("segredo1");
            afirmar(!hash.contains("segredo1"), "hash contém a senha pura");
            afirmar(enc.confere("segredo1", hash), "senha correta recusada");
            afirmar(!enc.confere("outra", hash), "senha errada aceita");
            afirmar(!enc.confere("segredo1", "lixo"), "hash corrompido aceito");
            afirmar(!hash.equals(enc.codificar("segredo1")), "sal não é aleatório");
        });

        s.teste("Cadastro de cliente grava hash, CPF formatado e e-mail em minúsculas", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Cliente c = f.cadastro().cadastrarCliente(dadosCliente("Maria", "52998224725", "MARIA@Email.com"));
            afirmar(c.getId() > 0, "id não gerado");
            igual(CPF_A, c.getCpf(), "cpf");
            igual("maria@email.com", c.getEmail(), "email");
            afirmar(!c.getSenhaHash().equals("123456"), "senha em texto puro");
        });

        s.teste("Cadastro: reúne todos os erros de validação", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            ValidacaoException e = lanca(ValidacaoException.class,
                    () -> f.cadastro().cadastrarCliente(new DadosCadastro().nome(" ").cpf("1").email("x")
                            .senha("123").confirmacaoSenha("123")), "deveria rejeitar");
            igual(4, e.getErros().size(), "quantidade de erros");
        });

        s.teste("Cadastro: e-mail, CPF duplicados e senhas divergentes", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            f.cadastro().cadastrarCliente(dadosCliente("Maria", CPF_A, "maria@email.com"));
            lanca(ValidacaoException.class, () -> f.cadastro().cadastrarCliente(
                    dadosCliente("Outra", CPF_B, "maria@email.com")), "e-mail duplicado");
            lanca(ValidacaoException.class, () -> f.cadastro().cadastrarCliente(
                    dadosCliente("Outra", CPF_A, "outra@email.com")), "CPF duplicado");
            lanca(ValidacaoException.class, () -> f.cadastro().cadastrarCliente(
                    dadosCliente("Outra", CPF_B, "b@email.com").confirmacaoSenha("diferente")), "senhas");
        });

        s.teste("Autenticação: sucesso e falha com mensagem única", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            f.cadastro().cadastrarCliente(dadosCliente("Maria", CPF_A, "maria@email.com"));
            Usuario u = f.autenticacao().autenticar(" Maria@Email.com ", "123456");
            igual("Maria", u.getNome(), "nome");
            CredenciaisInvalidasException e1 = lanca(CredenciaisInvalidasException.class,
                    () -> f.autenticacao().autenticar("maria@email.com", "errada"), "senha errada");
            CredenciaisInvalidasException e2 = lanca(CredenciaisInvalidasException.class,
                    () -> f.autenticacao().autenticar("naoexiste@email.com", "123456"), "usuário inexistente");
            igual(e1.getMessage(), e2.getMessage(), "mensagens devem ser iguais");
        });

        s.teste("Artista não aprovado não publica; após aprovação, publica", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Artista a = f.cadastro().cadastrarArtista(dadosCliente("Ana", CPF_A, "ana@email.com"));
            lanca(AcessoNegadoException.class, () -> f.catalogo().publicar(a, obra("Sertão", "Óleo", "100")), "pendente");
            igual(1, f.moderacao().listarArtistasPendentes(admin()).size(), "pendentes");
            f.moderacao().aprovarArtista(admin(), a.getId());
            Artista aprovada = (Artista) f.autenticacao().autenticar("ana@email.com", "123456");
            afirmar(aprovada.isAprovado(), "não aprovada");
            afirmar(f.catalogo().publicar(aprovada, obra("Sertão", "Óleo", "100")).getId() > 0, "obra sem id");
            lanca(ValidacaoException.class, () -> f.moderacao().aprovarArtista(admin(), 999), "id inexistente");
        });

        s.teste("Publicação: valida título, preço, dimensões e ano", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Artista a = artistaAprovada(f, "ana@email.com", CPF_A);
            ValidacaoException e = lanca(ValidacaoException.class, () -> f.catalogo().publicar(a,
                    new DadosObra().titulo("").tecnica("").ano(3000).larguraCm(0).alturaCm(10)
                            .preco(BigDecimal.ZERO)), "dados inválidos");
            igual(5, e.getErros().size(), "quantidade de erros");
        });

        s.teste("Pesquisa por termo, técnica e faixa de preço", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Artista a = artistaAprovada(f, "ana@email.com", CPF_A);
            f.catalogo().publicar(a, obra("Sertão Dourado", "Óleo sobre tela", "3500"));
            f.catalogo().publicar(a, obra("Flores do Cerrado", "Aquarela", "1200"));
            f.catalogo().publicar(a, obra("Pantanal", "Óleo sobre tela", "12000"));
            igual(3, f.catalogo().pesquisar(new FiltroObras()).size(), "todas");
            igual(1, f.catalogo().pesquisar(new FiltroObras().termo("cerrado")).size(), "termo");
            igual(1, f.catalogo().pesquisar(new FiltroObras().termo("ana")).size() > 0 ? 1 : 0, "termo por artista");
            igual(2, f.catalogo().pesquisar(new FiltroObras().tecnica("Óleo sobre tela")).size(), "técnica");
            igual(1, f.catalogo().pesquisar(new FiltroObras().precoMinimo(new BigDecimal("1000"))
                    .precoMaximo(new BigDecimal("2000"))).size(), "faixa");
        });

        s.teste("Carrinho: não duplica, soma o total e recusa obra vendida", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Artista a = artistaAprovada(f, "ana@email.com", CPF_A);
            ObraDeArte o1 = f.catalogo().publicar(a, obra("A", "Óleo", "100.50"));
            ObraDeArte o2 = f.catalogo().publicar(a, obra("B", "Óleo", "200.25"));
            Carrinho c = new Carrinho();
            afirmar(c.adicionar(o1), "primeira adição");
            afirmar(!c.adicionar(o1), "duplicada aceita");
            c.adicionar(o2);
            igual(new BigDecimal("300.75"), c.total(), "total");
            afirmar(c.remover(o1.getId()), "remoção");
            o2.marcarComoVendida();
            lanca(ObraIndisponivelException.class, () -> new Carrinho().adicionar(o2), "obra vendida");
        });

        s.teste("Compra: pedido PAGO, total correto, 1 certificado por obra e obras saem do catálogo", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Artista a = artistaAprovada(f, "ana@email.com", CPF_A);
            Cliente c = f.cadastro().cadastrarCliente(dadosCliente("Maria", CPF_B, "maria@email.com"));
            Carrinho carrinho = new Carrinho();
            carrinho.adicionar(f.catalogo().publicar(a, obra("A", "Óleo", "3500")));
            carrinho.adicionar(f.catalogo().publicar(a, obra("B", "Aquarela", "1200")));
            Pedido p = f.compras().finalizarCompra(c, carrinho, FormaPagamento.PIX);
            afirmar(p.getId() > 0, "pedido sem id");
            igual(StatusPedido.PAGO, p.getStatus(), "status");
            igual(new BigDecimal("4700"), p.getValorTotal(), "total");
            igual(2, p.getCertificados().size(), "certificados");
            afirmar(carrinho.isVazio(), "carrinho não esvaziou");
            igual(0, f.catalogo().pesquisar(new FiltroObras()).size(), "catálogo deveria estar vazio");
            igual(1, f.compras().listarPedidos(c).size(), "histórico");
            igual(2, f.compras().listarCertificados(c).size(), "certificados do cliente");
        });

        s.teste("Compra: carrinho vazio, sem pagamento e obra já vendida a outro cliente", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Artista a = artistaAprovada(f, "ana@email.com", CPF_A);
            Cliente c1 = f.cadastro().cadastrarCliente(dadosCliente("Maria", CPF_B, "maria@email.com"));
            Cliente c2 = f.cadastro().cadastrarCliente(dadosCliente("João", CPF_C, "joao@email.com"));
            ObraDeArte o = f.catalogo().publicar(a, obra("A", "Óleo", "100"));
            lanca(ValidacaoException.class, () -> f.compras().finalizarCompra(c1, new Carrinho(),
                    FormaPagamento.PIX), "carrinho vazio");
            Carrinho k1 = new Carrinho();
            k1.adicionar(o);
            Carrinho k2 = new Carrinho();
            k2.adicionar(o);
            lanca(ValidacaoException.class, () -> f.compras().finalizarCompra(c1, k1, null), "sem pagamento");
            f.compras().finalizarCompra(c1, k1, FormaPagamento.PIX);
            lanca(ObraIndisponivelException.class, () -> f.compras().finalizarCompra(c2, k2,
                    FormaPagamento.PIX), "segunda compra da mesma obra");
        });

        s.teste("Remoção: só o dono remove, e nunca obra vendida", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Artista dona = artistaAprovada(f, "ana@email.com", CPF_A);
            Artista outra = artistaAprovada(f, "carlos@email.com", CPF_B);
            ObraDeArte o = f.catalogo().publicar(dona, obra("A", "Óleo", "100"));
            lanca(AcessoNegadoException.class, () -> f.catalogo().remover(outra, o.getId()), "obra alheia");
            f.catalogo().remover(dona, o.getId());
            lanca(ValidacaoException.class, () -> f.catalogo().remover(dona, o.getId()), "já removida");
            ObraDeArte vendida = f.catalogo().publicar(dona, obra("B", "Óleo", "100"));
            vendida.marcarComoVendida();
            lanca(NegocioException.class, () -> f.catalogo().remover(dona, vendida.getId()), "obra vendida");
        });

        s.teste("Resumo do artista e transições de status do pedido", () -> {
            FabricaDeServicos f = FabricaDeServicos.emMemoria();
            Artista a = artistaAprovada(f, "ana@email.com", CPF_A);
            Cliente c = f.cadastro().cadastrarCliente(dadosCliente("Maria", CPF_B, "maria@email.com"));
            Carrinho k = new Carrinho();
            k.adicionar(f.catalogo().publicar(a, obra("A", "Óleo", "100")));
            f.catalogo().publicar(a, obra("B", "Óleo", "100"));
            Pedido p = f.compras().finalizarCompra(c, k, FormaPagamento.DINHEIRO);
            ResumoArtista r = f.catalogo().resumoDoArtista(a.getId());
            igual(2, r.getTotal(), "total");
            igual(1, r.getVendidas(), "vendidas");
            igual(1, r.getDisponiveis(), "disponíveis");
            f.compras().atualizarStatus(p.getId(), StatusPedido.PREPARANDO_ENVIO);
            lanca(NegocioException.class, () -> f.compras().atualizarStatus(p.getId(), StatusPedido.ENTREGUE), "pulou etapa");
            f.compras().atualizarStatus(p.getId(), StatusPedido.ENVIADO);
            lanca(NegocioException.class, () -> f.compras().atualizarStatus(p.getId(), StatusPedido.CANCELADO), "cancelar após envio");
            f.compras().atualizarStatus(p.getId(), StatusPedido.ENTREGUE);
            lanca(NegocioException.class, () -> f.compras().atualizarStatus(p.getId(), StatusPedido.ENVIADO), "status final");
        });
    }

    // ---- fábricas de dados de teste ----
    static DadosCadastro dadosCliente(String nome, String cpf, String email) {
        return new DadosCadastro().nome(nome).cpf(cpf).email(email).senha("123456").confirmacaoSenha("123456")
                .telefone("(62) 99999-0000");
    }

    static DadosObra obra(String titulo, String tecnica, String preco) {
        return new DadosObra().titulo(titulo).tecnica(tecnica).estilo("Realismo").local("Goiânia").ano(2023)
                .larguraCm(80).alturaCm(60).preco(new BigDecimal(preco));
    }

    private static Administrador admin() {
        return new Administrador(1, "Admin", CPF_C, "admin@finearts.com", "x", LocalDate.now());
    }

    private static Artista artistaAprovada(FabricaDeServicos f, String email, String cpf) {
        Artista a = f.cadastro().cadastrarArtista(dadosCliente("Artista " + email, cpf, email));
        f.moderacao().aprovarArtista(admin(), a.getId());
        return (Artista) f.autenticacao().autenticar(email, "123456");
    }
}
