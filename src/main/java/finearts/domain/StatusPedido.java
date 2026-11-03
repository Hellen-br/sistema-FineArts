package finearts.domain;

/** Ciclo de vida do pedido (RF012). A regra de transição vive no próprio enum. */
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO, PAGO, PREPARANDO_ENVIO, ENVIADO, ENTREGUE, CANCELADO;

    public boolean isFinal() {
        return this == ENTREGUE || this == CANCELADO;
    }

    /** Avança uma etapa por vez; cancelar só é possível antes do envio. */
    public boolean podeIrPara(StatusPedido novo) {
        if (isFinal() || novo == this) {
            return false;
        }
        if (novo == CANCELADO) {
            return ordinal() < ENVIADO.ordinal();
        }
        return novo.ordinal() == ordinal() + 1;
    }
}
