package finearts.service;

import java.math.BigDecimal;

/** Estatísticas exibidas na área do artista (RF014). */
public final class ResumoArtista {
    private final int total;
    private final int vendidas;
    private final BigDecimal valorVendido;
    private final BigDecimal repasse;

    public ResumoArtista(int total, int vendidas, BigDecimal valorVendido, BigDecimal repasse) {
        this.total = total;
        this.vendidas = vendidas;
        this.valorVendido = valorVendido;
        this.repasse = repasse;
    }

    public int getTotal() { return total; }
    public int getVendidas() { return vendidas; }
    public int getDisponiveis() { return total - vendidas; }
    /** Soma dos preços das obras vendidas. */
    public BigDecimal getValorVendido() { return valorVendido; }
    /** Valor líquido do artista, já descontada a comissão da plataforma. */
    public BigDecimal getRepasse() { return repasse; }
}
