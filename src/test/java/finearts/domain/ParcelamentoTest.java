package finearts.domain;

import static finearts.testes.Dados.assertValor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import finearts.domain.excecao.ValidacaoException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Parcelamento – divisão em parcelas")
class ParcelamentoTest {

    @Test
    void umaParcelaDevolveOTotal() {
        List<BigDecimal> p = Parcelamento.dividir(new BigDecimal("50"), 1);
        assertEquals(1, p.size());
        assertValor("50.00", p.get(0));
    }

    @Test
    void divisaoExataGeraParcelasIguais() {
        List<BigDecimal> p = Parcelamento.dividir(new BigDecimal("1200.00"), 4);
        assertEquals(4, p.size());
        p.forEach(v -> assertValor("300.00", v));
    }

    @Test
    void centavosRestantesVaoParaAPrimeiraParcela() {
        List<BigDecimal> p = Parcelamento.dividir(new BigDecimal("1000.00"), 3);
        assertValor("333.34", p.get(0));
        assertValor("333.33", p.get(1));
        assertValor("333.33", p.get(2));
    }

    @ParameterizedTest(name = "total {0} em {1}x: a soma das parcelas é o total")
    @CsvSource({"1000.00, 3", "4700.00, 12", "999.99, 7", "12000.00, 12", "250.50, 2", "100.00, 1"})
    void somaDasParcelasSempreIgualAoTotal(String total, int parcelas) {
        BigDecimal soma = Parcelamento.dividir(new BigDecimal(total), parcelas).stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertValor(total, soma);
    }

    @Test
    void parcelaMinimaDeCemReais() {
        assertEquals(2, Parcelamento.dividir(new BigDecimal("200.00"), 2).size());   // exatamente R$ 100,00
        assertThrows(ValidacaoException.class, () -> Parcelamento.dividir(new BigDecimal("150.00"), 2));
    }

    @ParameterizedTest(name = "{0} parcelas é inválido")
    @CsvSource({"0", "-1", "13", "100"})
    void rejeitaQuantidadeInvalida(int parcelas) {
        assertThrows(ValidacaoException.class, () -> Parcelamento.dividir(new BigDecimal("5000"), parcelas));
    }

    @Test
    void rejeitaTotalNuloZeroOuNegativo() {
        assertThrows(ValidacaoException.class, () -> Parcelamento.dividir(null, 1));
        assertThrows(ValidacaoException.class, () -> Parcelamento.dividir(BigDecimal.ZERO, 1));
        assertThrows(ValidacaoException.class, () -> Parcelamento.dividir(new BigDecimal("-10"), 1));
    }

    @ParameterizedTest(name = "máximo de parcelas para {0} = {1}")
    @CsvSource({"50, 1", "199.99, 1", "250, 2", "1000, 10", "5000, 12", "100000, 12"})
    void calculaMaximoPermitido(String total, int esperado) {
        assertEquals(esperado, Parcelamento.maximoPermitido(new BigDecimal(total)));
    }

    @Test
    void maximoPermitidoParaValorNuloEUm() {
        assertEquals(1, Parcelamento.maximoPermitido(null));
    }
}
