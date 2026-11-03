package finearts.domain;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

/**
 * Critérios de pesquisa do catálogo. Substitui os métodos quase idênticos
 * buscar / filtrarTecnica / filtrarPreco / listarPorArtista do DAO do desktop.
 */
public final class FiltroObras {
    private String termo;
    private String tecnica;
    private BigDecimal precoMinimo;
    private BigDecimal precoMaximo;
    private Integer artistaId;
    private boolean incluirIndisponiveis;

    public FiltroObras termo(String v) { this.termo = v; return this; }
    public FiltroObras tecnica(String v) { this.tecnica = v; return this; }
    public FiltroObras precoMinimo(BigDecimal v) { this.precoMinimo = v; return this; }
    public FiltroObras precoMaximo(BigDecimal v) { this.precoMaximo = v; return this; }
    public FiltroObras artista(int id) { this.artistaId = id; return this; }
    public FiltroObras incluindoIndisponiveis() { this.incluirIndisponiveis = true; return this; }

    public Optional<String> getTermo() { return textoOuVazio(termo); }
    public Optional<String> getTecnica() { return textoOuVazio(tecnica); }
    public Optional<BigDecimal> getPrecoMinimo() { return Optional.ofNullable(precoMinimo); }
    public Optional<BigDecimal> getPrecoMaximo() { return Optional.ofNullable(precoMaximo); }
    public Optional<Integer> getArtistaId() { return Optional.ofNullable(artistaId); }
    public boolean isIncluirIndisponiveis() { return incluirIndisponiveis; }

    /** Avalia o filtro em memória (usado pelo repositório em memória). */
    public boolean aceita(ObraDeArte o) {
        if (!incluirIndisponiveis && !o.isDisponivel()) return false;
        if (artistaId != null && o.getArtistaId() != artistaId) return false;
        if (getTecnica().isPresent() && !o.getTecnica().equalsIgnoreCase(getTecnica().get())) return false;
        if (precoMinimo != null && o.getPreco().compareTo(precoMinimo) < 0) return false;
        if (precoMaximo != null && o.getPreco().compareTo(precoMaximo) > 0) return false;
        if (getTermo().isPresent()) {
            String t = getTermo().get().toLowerCase(Locale.ROOT);
            return contem(o.getTitulo(), t) || contem(o.getTecnica(), t)
                    || contem(o.getEstilo(), t) || contem(o.getNomeArtista(), t);
        }
        return true;
    }

    private static boolean contem(String texto, String trecho) {
        return texto != null && texto.toLowerCase(Locale.ROOT).contains(trecho);
    }

    private static Optional<String> textoOuVazio(String s) {
        return s == null || s.trim().isEmpty() ? Optional.empty() : Optional.of(s.trim());
    }
}
