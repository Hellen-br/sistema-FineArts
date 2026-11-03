package finearts.service;

import static finearts.testes.Dados.CPF_1;
import static finearts.testes.Dados.CPF_2;
import static finearts.testes.Dados.CPF_3;
import static finearts.testes.Dados.SENHA;
import static finearts.testes.Dados.admin;
import static finearts.testes.Dados.artistaAprovada;
import static finearts.testes.Dados.assertValor;
import static finearts.testes.Dados.cadastro;
import static finearts.testes.Dados.dadosObra;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import finearts.app.FabricaDeServicos;
import finearts.domain.Artista;
import finearts.domain.Carrinho;
import finearts.domain.Cliente;
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
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Testes de serviço com repositórios em memória (sem banco de dados). */
@DisplayName("Serviços da aplicação")
class ServicosTest {
    private FabricaDeServicos f;

    @BeforeEach
    void novoCenario() {
        f = FabricaDeServicos.emMemoria();
    }

    @Nested
    @DisplayName("Cadastro e autenticação (RF001, RF004, RNF001, RNF004)")
    class CadastroEAutenticacao {
        @Test
        void cadastraClienteComSenhaProtegidaECpfFormatado() {
            Cliente c = f.cadastro().cadastrarCliente(cadastro("Maria", "52998224725", "MARIA@Email.com"));
            assertTrue(c.getId() > 0);
            assertEquals(CPF_1, c.getCpf());
            assertEquals("maria@email.com", c.getEmail());
            assertNotEquals(SENHA, c.getSenhaHash());
        }

        @Test
        void recusaEmailOuCpfDuplicado() {
            f.cadastro().cadastrarCliente(cadastro("Maria", CPF_1, "maria@email.com"));
            assertThrows(ValidacaoException.class,
                    () -> f.cadastro().cadastrarCliente(cadastro("Outra", CPF_2, "MARIA@email.com")));
            assertThrows(ValidacaoException.class,
                    () -> f.cadastro().cadastrarCliente(cadastro("Outra", CPF_1, "outra@email.com")));
        }

        @Test
        void loginComSucessoIgnoraMaiusculasEEspacosNoEmail() {
            f.cadastro().cadastrarCliente(cadastro("Maria", CPF_1, "maria@email.com"));
            Usuario u = f.autenticacao().autenticar("  MARIA@email.com ", SENHA);
            assertEquals("Maria", u.getNome());
        }

        @Test
        void loginFalhoTemMensagemUnicaParaSenhaErradaEUsuarioInexistente() {
            f.cadastro().cadastrarCliente(cadastro("Maria", CPF_1, "maria@email.com"));
            CredenciaisInvalidasException a = assertThrows(CredenciaisInvalidasException.class,
                    () -> f.autenticacao().autenticar("maria@email.com", "errada"));
            CredenciaisInvalidasException b = assertThrows(CredenciaisInvalidasException.class,
                    () -> f.autenticacao().autenticar("naoexiste@email.com", SENHA));
            assertEquals(a.getMessage(), b.getMessage());
        }

        @Test
        void loginComEntradaNulaEhRecusado() {
            assertThrows(CredenciaisInvalidasException.class, () -> f.autenticacao().autenticar(null, SENHA));
            assertThrows(CredenciaisInvalidasException.class, () -> f.autenticacao().autenticar("a@b.com", null));
        }
    }

    @Nested
    @DisplayName("Moderação e catálogo (RF003, RF007, RF013)")
    class ModeracaoECatalogo {
        @Test
        void artistaPendenteNaoPublicaAteSerAprovado() {
            Artista a = f.cadastro().cadastrarArtista(cadastro("Ana", CPF_1, "ana@email.com"));
            assertThrows(AcessoNegadoException.class,
                    () -> f.catalogo().publicar(a, dadosObra("Sertão", "Óleo", "100")));
            assertEquals(1, f.moderacao().listarArtistasPendentes(admin()).size());
            f.moderacao().aprovarArtista(admin(), a.getId());
            assertEquals(0, f.moderacao().listarArtistasPendentes(admin()).size());
        }

        @Test
        void aprovarArtistaInexistenteFalha() {
            assertThrows(ValidacaoException.class, () -> f.moderacao().aprovarArtista(admin(), 999));
        }

        @Test
        void publicaObraEAplicaPadroesParaCamposOpcionais() {
            Artista a = artistaAprovada(f, "ana@email.com", CPF_1);
            ObraDeArte o = f.catalogo().publicar(a, dadosObra("  Sertão  ", "Óleo", "100").estilo(" ").local(null));
            assertTrue(o.getId() > 0);
            assertEquals("Sertão", o.getTitulo());
            assertEquals("Não informado", o.getEstilo());
            assertEquals("Não informado", o.getLocal());
            assertEquals(a.getId(), o.getArtistaId());
        }

        @Test
        void pesquisaPorTermoTecnicaEFaixaDePreco() {
            Artista a = artistaAprovada(f, "ana@email.com", CPF_1);
            f.catalogo().publicar(a, dadosObra("Sertão Dourado", "Óleo sobre tela", "3500"));
            f.catalogo().publicar(a, dadosObra("Flores do Cerrado", "Aquarela", "1200"));
            f.catalogo().publicar(a, dadosObra("Pantanal", "Óleo sobre tela", "12000"));
            assertEquals(3, f.catalogo().pesquisar(new FiltroObras()).size());
            assertEquals(1, f.catalogo().pesquisar(new FiltroObras().termo("cerrado")).size());
            assertEquals(2, f.catalogo().pesquisar(new FiltroObras().tecnica("Óleo sobre tela")).size());
            assertEquals(1, f.catalogo().pesquisar(new FiltroObras().precoMinimo(new BigDecimal("1000"))
                    .precoMaximo(new BigDecimal("2000"))).size());
        }

        @Test
        void somenteODonoRemoveEApenasObraNaoVendida() {
            Artista dona = artistaAprovada(f, "ana@email.com", CPF_1);
            Artista outra = artistaAprovada(f, "carlos@email.com", CPF_2);
            ObraDeArte o = f.catalogo().publicar(dona, dadosObra("A", "Óleo", "100"));
            assertThrows(AcessoNegadoException.class, () -> f.catalogo().remover(outra, o.getId()));
            ObraDeArte vendida = f.catalogo().publicar(dona, dadosObra("B", "Óleo", "100"));
            vendida.marcarComoVendida();
            assertThrows(NegocioException.class, () -> f.catalogo().remover(dona, vendida.getId()));
            f.catalogo().remover(dona, o.getId());
            assertThrows(ValidacaoException.class, () -> f.catalogo().remover(dona, o.getId()));
        }
    }

    @Nested
    @DisplayName("Compra, certificados e pedidos (RF009–RF012, RF014, RF016)")
    class Compra {
        private Artista artista;
        private Cliente cliente;

        @BeforeEach
        void preparar() {
            artista = artistaAprovada(f, "ana@email.com", CPF_1);
            cliente = f.cadastro().cadastrarCliente(cadastro("Maria", CPF_2, "maria@email.com"));
        }

        private Carrinho carrinhoCom(String... precos) {
            Carrinho c = new Carrinho();
            int n = 0;
            for (String preco : precos) {
                c.adicionar(f.catalogo().publicar(artista, dadosObra("Obra " + (++n), "Óleo", preco)));
            }
            return c;
        }

        @Test
        void finalizaCompraGerandoPedidoPagoCertificadosEBaixaNoCatalogo() {
            Carrinho c = carrinhoCom("3500", "1200");
            Pedido p = f.compras().finalizarCompra(cliente, c, FormaPagamento.PIX);
            assertTrue(p.getId() > 0);
            assertEquals(StatusPedido.PAGO, p.getStatus());
            assertValor("4700", p.getValorTotal());
            assertEquals(2, p.getCertificados().size());
            assertTrue(c.isVazio());
            assertEquals(0, f.catalogo().pesquisar(new FiltroObras()).size());
            assertEquals(1, f.compras().listarPedidos(cliente).size());
            assertEquals(2, f.compras().listarCertificados(cliente).size());
        }

        @Test
        void recusaCarrinhoVazioESemFormaDePagamento() {
            assertThrows(ValidacaoException.class,
                    () -> f.compras().finalizarCompra(cliente, new Carrinho(), FormaPagamento.PIX));
            Carrinho c = carrinhoCom("100");
            assertThrows(ValidacaoException.class, () -> f.compras().finalizarCompra(cliente, c, null));
            assertFalse(c.isVazio());   // nada foi alterado
        }

        @Test
        void naoVendeAMesmaObraParaDuasPessoas() {
            Cliente outro = f.cadastro().cadastrarCliente(cadastro("João", CPF_3, "joao@email.com"));
            Carrinho c1 = carrinhoCom("100");
            Carrinho c2 = new Carrinho();
            c2.adicionar(c1.getItens().get(0));
            f.compras().finalizarCompra(cliente, c1, FormaPagamento.PIX);
            assertThrows(ObraIndisponivelException.class,
                    () -> f.compras().finalizarCompra(outro, c2, FormaPagamento.DINHEIRO));
        }

        @Test
        void simulaParcelamentoDoCarrinho() {
            Carrinho c = carrinhoCom("1000");
            List<BigDecimal> parcelas = f.compras().simularParcelamento(c, 3);
            assertEquals(3, parcelas.size());
            assertValor("333.34", parcelas.get(0));
            assertThrows(ValidacaoException.class, () -> f.compras().simularParcelamento(new Carrinho(), 2));
            assertThrows(ValidacaoException.class, () -> f.compras().simularParcelamento(c, 13));
        }

        @Test
        void resumoDoArtistaCalculaVendasERepasse() {
            Carrinho c = carrinhoCom("1000", "2000");
            f.catalogo().publicar(artista, dadosObra("Sem vender", "Óleo", "500"));
            f.compras().finalizarCompra(cliente, c, FormaPagamento.CARTAO_CREDITO);
            ResumoArtista r = f.catalogo().resumoDoArtista(artista.getId());
            assertEquals(3, r.getTotal());
            assertEquals(2, r.getVendidas());
            assertEquals(1, r.getDisponiveis());
            assertValor("3000", r.getValorVendido());
            assertValor("2550.00", r.getRepasse());   // 3000 - 15%
        }

        @Test
        void acompanhaStatusDoPedidoEmOrdem() {
            Pedido p = f.compras().finalizarCompra(cliente, carrinhoCom("100"), FormaPagamento.PIX);
            f.compras().atualizarStatus(p.getId(), StatusPedido.PREPARANDO_ENVIO);
            assertThrows(NegocioException.class, () -> f.compras().atualizarStatus(p.getId(), StatusPedido.ENTREGUE));
            f.compras().atualizarStatus(p.getId(), StatusPedido.ENVIADO);
            f.compras().atualizarStatus(p.getId(), StatusPedido.ENTREGUE);
            assertThrows(NegocioException.class, () -> f.compras().atualizarStatus(p.getId(), StatusPedido.ENVIADO));
            assertThrows(ValidacaoException.class, () -> f.compras().atualizarStatus(999, StatusPedido.ENVIADO));
        }
    }
}
