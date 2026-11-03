package finearts.service;

import static finearts.testes.Dados.dadosObra;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import finearts.domain.excecao.ValidacaoException;
import java.math.BigDecimal;
import java.time.Year;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("ValidadorObra – regras de RF003")
class ValidadorObraTest {
    private final ValidadorObra validador = new ValidadorObra();

    @Test
    void aceitaObraValida() {
        assertDoesNotThrow(() -> validador.validar(dadosObra("Sertão", "Óleo", "100")));
    }

    @Test
    void aceitaAnoAtual() {
        assertDoesNotThrow(() -> validador.validar(dadosObra("A", "B", "1").ano(Year.now().getValue())));
    }

    @ParameterizedTest(name = "preço inválido: {0}")
    @ValueSource(strings = {"0", "-1", "-0.01"})
    void rejeitaPrecoZeroOuNegativo(String preco) {
        assertThrows(ValidacaoException.class, () -> validador.validar(dadosObra("A", "B", preco)));
    }

    @Test
    void rejeitaPrecoNulo() {
        assertThrows(ValidacaoException.class, () -> validador.validar(dadosObra("A", "B", "1").preco(null)));
    }

    @ParameterizedTest(name = "ano inválido: {0}")
    @ValueSource(ints = {0, 999, 3000})
    void rejeitaAnoForaDoIntervalo(int ano) {
        assertThrows(ValidacaoException.class, () -> validador.validar(dadosObra("A", "B", "1").ano(ano)));
    }

    @Test
    void rejeitaDimensoesNaoPositivas() {
        assertThrows(ValidacaoException.class, () -> validador.validar(dadosObra("A", "B", "1").larguraCm(0)));
        assertThrows(ValidacaoException.class, () -> validador.validar(dadosObra("A", "B", "1").alturaCm(-3)));
    }

    @Test
    void reuneTodosOsErros() {
        ValidacaoException e = assertThrows(ValidacaoException.class, () -> validador.validar(
                dadosObra("", "", "1").ano(3000).larguraCm(0).preco(BigDecimal.ZERO)));
        assertEquals(5, e.getErros().size());
    }
}
