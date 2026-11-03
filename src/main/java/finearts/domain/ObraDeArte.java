package finearts.domain;

import finearts.domain.excecao.ObraIndisponivelException;
import java.math.BigDecimal;

/** Obra à venda. Construída por Builder (evita o construtor de 10 parâmetros do desktop). */
public final class ObraDeArte {
    private int id;
    private final int artistaId;
    private final String nomeArtista;
    private final String titulo;
    private final String tecnica;
    private final String estilo;
    private final String local;
    private final int ano;
    private final Dimensoes dimensoes;
    private final BigDecimal preco;
    private boolean disponivel;

    private ObraDeArte(Builder b) {
        this.id = b.id;
        this.artistaId = b.artistaId;
        this.nomeArtista = b.nomeArtista;
        this.titulo = b.titulo;
        this.tecnica = b.tecnica;
        this.estilo = b.estilo;
        this.local = b.local;
        this.ano = b.ano;
        this.dimensoes = b.dimensoes;
        this.preco = b.preco;
        this.disponivel = b.disponivel;
    }

    public static Builder builder() { return new Builder(); }

    public void marcarComoVendida() {
        if (!disponivel) {
            throw new ObraIndisponivelException(titulo);
        }
        this.disponivel = false;
    }

    public void definirId(int novoId) {
        if (id != 0) {
            throw new IllegalStateException("Identificador já definido.");
        }
        this.id = novoId;
    }

    public int getId() { return id; }
    public int getArtistaId() { return artistaId; }
    public String getNomeArtista() { return nomeArtista; }
    public String getTitulo() { return titulo; }
    public String getTecnica() { return tecnica; }
    public String getEstilo() { return estilo; }
    public String getLocal() { return local; }
    public int getAno() { return ano; }
    public Dimensoes getDimensoes() { return dimensoes; }
    public BigDecimal getPreco() { return preco; }
    public boolean isDisponivel() { return disponivel; }

    public static final class Builder {
        private int id;
        private int artistaId;
        private String nomeArtista;
        private String titulo;
        private String tecnica;
        private String estilo;
        private String local;
        private int ano;
        private Dimensoes dimensoes;
        private BigDecimal preco;
        private boolean disponivel = true;

        public Builder id(int v) { this.id = v; return this; }
        public Builder artistaId(int v) { this.artistaId = v; return this; }
        public Builder nomeArtista(String v) { this.nomeArtista = v; return this; }
        public Builder titulo(String v) { this.titulo = v; return this; }
        public Builder tecnica(String v) { this.tecnica = v; return this; }
        public Builder estilo(String v) { this.estilo = v; return this; }
        public Builder local(String v) { this.local = v; return this; }
        public Builder ano(int v) { this.ano = v; return this; }
        public Builder dimensoes(Dimensoes v) { this.dimensoes = v; return this; }
        public Builder preco(BigDecimal v) { this.preco = v; return this; }
        public Builder disponivel(boolean v) { this.disponivel = v; return this; }
        public ObraDeArte build() { return new ObraDeArte(this); }
    }
}
