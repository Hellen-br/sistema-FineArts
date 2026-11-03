package finearts.domain;

import java.math.BigDecimal;

/** Obra comprada com o preço praticado no momento da compra. */
public final class ItemPedido {
    private final ObraDeArte obra;
    private final BigDecimal precoUnitario;

    public ItemPedido(ObraDeArte obra, BigDecimal precoUnitario) {
        this.obra = obra;
        this.precoUnitario = precoUnitario;
    }

    public ObraDeArte getObra() { return obra; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
}
