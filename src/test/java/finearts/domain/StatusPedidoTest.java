package finearts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("StatusPedido – transições do pedido (RF012)")
class StatusPedidoTest {

    @ParameterizedTest(name = "{0} → {1} permitido = {2}")
    @CsvSource({
            "AGUARDANDO_PAGAMENTO, PAGO, true",
            "AGUARDANDO_PAGAMENTO, CANCELADO, true",
            "PAGO, PREPARANDO_ENVIO, true",
            "PAGO, CANCELADO, true",
            "PREPARANDO_ENVIO, ENVIADO, true",
            "PREPARANDO_ENVIO, CANCELADO, true",
            "ENVIADO, ENTREGUE, true",
            "PAGO, ENVIADO, false",
            "PAGO, ENTREGUE, false",
            "PAGO, PAGO, false",
            "ENVIADO, CANCELADO, false",
            "ENVIADO, PAGO, false",
            "ENTREGUE, ENVIADO, false",
            "ENTREGUE, CANCELADO, false",
            "CANCELADO, PAGO, false"
    })
    void validaTransicoes(StatusPedido de, StatusPedido para, boolean permitido) {
        assertEquals(permitido, de.podeIrPara(para));
    }

    @Test
    void apenasEntregueECanceladoSaoFinais() {
        assertTrue(StatusPedido.ENTREGUE.isFinal());
        assertTrue(StatusPedido.CANCELADO.isFinal());
        assertFalse(StatusPedido.PAGO.isFinal());
        assertFalse(StatusPedido.ENVIADO.isFinal());
    }
}
