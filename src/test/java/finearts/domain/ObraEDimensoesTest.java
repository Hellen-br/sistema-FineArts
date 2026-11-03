package finearts.domain;

import static finearts.testes.Dados.obra;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import finearts.domain.excecao.ObraIndisponivelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("ObraDeArte e Dimensoes")
class ObraEDimensoesTest {

    @ParameterizedTest(name = "dimensões {0} x {1} são inválidas")
    @CsvSource({"0, 10", "10, 0", "-5, 10", "10, -5"})
    void dimensoesDevemSerPositivas(double largura, double altura) {
        assertThrows(IllegalArgumentException.class, () -> new Dimensoes(largura, altura));
    }

    @Test
    void dimensoesTemTextoLegivel() {
        assertEquals("80.0 x 60.0 cm", new Dimensoes(80, 60).toString());
    }

    @Test
    void obraNovaEstaDisponivel() {
        assertTrue(obra(1, "A", "Óleo", "10").isDisponivel());
    }

    @Test
    void marcarComoVendidaTornaIndisponivel() {
        ObraDeArte o = obra(1, "A", "Óleo", "10");
        o.marcarComoVendida();
        assertFalse(o.isDisponivel());
    }

    @Test
    void naoPodeVenderDuasVezes() {
        ObraDeArte o = obra(1, "A", "Óleo", "10");
        o.marcarComoVendida();
        assertThrows(ObraIndisponivelException.class, o::marcarComoVendida);
    }
}
