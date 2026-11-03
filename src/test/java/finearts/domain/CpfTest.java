package finearts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Cpf – validação e formatação")
class CpfTest {

    @ParameterizedTest(name = "CPF válido: {0}")
    @ValueSource(strings = {"529.982.247-25", "52998224725", "168.995.350-09", "111.444.777-35"})
    void aceitaCpfValido(String cpf) {
        assertTrue(Cpf.valido(cpf));
    }

    @ParameterizedTest(name = "CPF inválido: {0}")
    @ValueSource(strings = {"111.111.111-11", "000.000.000-00", "123.456.789-00", "529.982.247-24", "1234", "abc"})
    void rejeitaCpfInvalido(String cpf) {
        assertFalse(Cpf.valido(cpf));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void rejeitaNuloEVazio(String cpf) {
        assertFalse(Cpf.valido(cpf));
    }

    @Test
    void formataComPontosETraco() {
        assertEquals("529.982.247-25", Cpf.formatar("52998224725"));
        assertEquals("529.982.247-25", Cpf.formatar("529.982.247-25"));
    }
}
