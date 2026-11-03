package finearts.domain;

import static finearts.testes.Dados.obra;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FiltroObras – critérios do catálogo (RF007)")
class FiltroObrasTest {
    private ObraDeArte sertao;

    @BeforeEach
    void criarObra() {
        sertao = obra(1, "Sertão Dourado", "Óleo sobre tela", "3500");
    }

    @Test
    void filtroVazioAceitaObraDisponivel() {
        assertTrue(new FiltroObras().aceita(sertao));
    }

    @Test
    void porPadraoIgnoraObraVendida() {
        sertao.marcarComoVendida();
        assertFalse(new FiltroObras().aceita(sertao));
        assertTrue(new FiltroObras().incluindoIndisponiveis().aceita(sertao));
    }

    @Test
    void tecnicaIgnoraMaiusculas() {
        assertTrue(new FiltroObras().tecnica("óleo sobre tela").aceita(sertao));
        assertFalse(new FiltroObras().tecnica("Aquarela").aceita(sertao));
    }

    @Test
    void faixaDePrecoIncluiOsLimites() {
        assertTrue(new FiltroObras().precoMinimo(new BigDecimal("3500")).precoMaximo(new BigDecimal("3500")).aceita(sertao));
        assertFalse(new FiltroObras().precoMinimo(new BigDecimal("3500.01")).aceita(sertao));
        assertFalse(new FiltroObras().precoMaximo(new BigDecimal("3499.99")).aceita(sertao));
    }

    @Test
    void termoBuscaEmTituloTecnicaEstiloEArtista() {
        assertTrue(new FiltroObras().termo("sertão").aceita(sertao));
        assertTrue(new FiltroObras().termo("TELA").aceita(sertao));
        assertTrue(new FiltroObras().termo("realismo").aceita(sertao));
        assertTrue(new FiltroObras().termo("ana lima").aceita(sertao));
        assertFalse(new FiltroObras().termo("pantanal").aceita(sertao));
    }

    @Test
    void termoEmBrancoEhIgnorado() {
        assertTrue(new FiltroObras().termo("   ").aceita(sertao));
    }

    @Test
    void filtraPorArtista() {
        assertTrue(new FiltroObras().artista(1).aceita(sertao));
        assertFalse(new FiltroObras().artista(2).aceita(sertao));
    }

    @Test
    void criteriosSaoCombinados() {
        FiltroObras f = new FiltroObras().tecnica("Óleo sobre tela").termo("sertão").precoMaximo(new BigDecimal("4000"));
        assertTrue(f.aceita(sertao));
        assertFalse(f.precoMaximo(new BigDecimal("1000")).aceita(sertao));
    }
}
