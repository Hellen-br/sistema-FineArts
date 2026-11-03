package finearts.service;

import java.math.BigDecimal;

/** Dados já tipados de uma nova obra. Converter texto digitado em números é tarefa da interface. */
public class DadosObra {
    private String titulo;
    private String tecnica;
    private String estilo;
    private String local;
    private int ano;
    private double larguraCm;
    private double alturaCm;
    private BigDecimal preco;

    public DadosObra titulo(String v) { this.titulo = v; return this; }
    public DadosObra tecnica(String v) { this.tecnica = v; return this; }
    public DadosObra estilo(String v) { this.estilo = v; return this; }
    public DadosObra local(String v) { this.local = v; return this; }
    public DadosObra ano(int v) { this.ano = v; return this; }
    public DadosObra larguraCm(double v) { this.larguraCm = v; return this; }
    public DadosObra alturaCm(double v) { this.alturaCm = v; return this; }
    public DadosObra preco(BigDecimal v) { this.preco = v; return this; }

    public String getTitulo() { return titulo; }
    public String getTecnica() { return tecnica; }
    public String getEstilo() { return estilo; }
    public String getLocal() { return local; }
    public int getAno() { return ano; }
    public double getLarguraCm() { return larguraCm; }
    public double getAlturaCm() { return alturaCm; }
    public BigDecimal getPreco() { return preco; }
}
