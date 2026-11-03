package finearts.domain;

import static finearts.testes.Dados.assertValor;
import static finearts.testes.Dados.obra;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import finearts.domain.excecao.NegocioException;
import finearts.domain.excecao.ValidacaoException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pedido – criação, totais, certificados e status (RF010, RF011, RF012)")
class PedidoTest {
    private Pedido pedido;

    @BeforeEach
    void criarPedido() {
        pedido = Pedido.novo(7, Arrays.asList(obra(1, "A", "Óleo", "3500"), obra(2, "B", "Aquarela", "1200")),
                FormaPagamento.PIX);
    }

    @Test
    void pedidoNovoNasceComoPagoEComDadosDoCliente() {
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        assertEquals(7, pedido.getClienteId());
        assertEquals(FormaPagamento.PIX, pedido.getFormaPagamento());
        assertNotNull(pedido.getDataHora());
        assertEquals(0, pedido.getId());
    }

    @Test
    void valorTotalEhASomaDosItens() {
        assertEquals(2, pedido.getItens().size());
        assertValor("4700", pedido.getValorTotal());
    }

    @Test
    void emiteUmCertificadoPorObra() {
        assertEquals(2, pedido.getCertificados().size());
        pedido.getCertificados().forEach(c -> assertEquals(7, c.getCompradorId()));
        assertEquals(2, pedido.getCertificados().stream().map(Certificado::getCodigo).distinct().count());
    }

    @Test
    void calculaComissaoERepasseDoPedido() {
        assertValor("705.00", pedido.getComissaoPlataforma());     // 525,00 + 180,00
        assertValor("3995.00", pedido.getRepasseAosArtistas());    // 4700 - 705
    }

    @Test
    void naoCriaPedidoSemObras() {
        assertThrows(ValidacaoException.class, () -> Pedido.novo(1, Collections.emptyList(), FormaPagamento.PIX));
        assertThrows(ValidacaoException.class, () -> Pedido.novo(1, null, FormaPagamento.PIX));
    }

    @Test
    void avancaStatusEmOrdem() {
        pedido.atualizarStatus(StatusPedido.PREPARANDO_ENVIO);
        pedido.atualizarStatus(StatusPedido.ENVIADO);
        pedido.atualizarStatus(StatusPedido.ENTREGUE);
        assertEquals(StatusPedido.ENTREGUE, pedido.getStatus());
    }

    @Test
    void recusaTransicaoInvalidaMantendoOStatus() {
        assertThrows(NegocioException.class, () -> pedido.atualizarStatus(StatusPedido.ENTREGUE));
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
    }

    @Test
    void identificadorSoPodeSerDefinidoUmaVez() {
        pedido.definirId(10);
        assertEquals(10, pedido.getId());
        assertThrows(IllegalStateException.class, () -> pedido.definirId(11));
    }

    @Test
    void colecoesExpostasSaoSomenteLeitura() {
        assertThrows(UnsupportedOperationException.class, () -> pedido.getItens().clear());
        assertThrows(UnsupportedOperationException.class, () -> pedido.getCertificados().clear());
    }
}
