package finearts.domain;

import static finearts.testes.Dados.assertValor;
import static org.junit.jupiter.api.Assertions.assertThrows;

import finearts.domain.excecao.ValidacaoException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("ComissaoPlataforma – cálculo de comissão e repasse (15%)")
class ComissaoPlataformaTest {
    private final ComissaoPlataforma regra = new ComissaoPlataforma();

    @ParameterizedTest(name = "venda {0} → comissão {1} e repasse {2}")
    @CsvSource({
            "3500.00, 525.00, 2975.00",
            "1200.00, 180.00, 1020.00",
            "10.00, 1.50, 8.50",
            "100.10, 15.02, 85.08",   // 15,015 arredonda para cima (HALF_UP)
            "0.03, 0.00, 0.03",       // 0,0045 arredonda para baixo
            "0, 0.00, 0.00"
    })
    void calculaComissaoERepasse(String venda, String comissao, String repasse) {
        BigDecimal v = new BigDecimal(venda);
        assertValor(comissao, regra.sobre(v));
        assertValor(repasse, regra.repasseAoArtista(v));
    }

    @Test
    void comissaoMaisRepasseSempreIgualAoValorDaVenda() {
        BigDecimal venda = new BigDecimal("1234.56");
        assertValor("1234.56", regra.sobre(venda).add(regra.repasseAoArtista(venda)));
    }

    @Test
    void aceitaPercentualPersonalizado() {
        assertValor("20.00", new ComissaoPlataforma(new BigDecimal("0.10")).sobre(new BigDecimal("200")));
    }

    @ParameterizedTest(name = "percentual inválido: {0}")
    @CsvSource({"-0.01", "1.01", "2"})
    void rejeitaPercentualForaDoIntervalo(String p) {
        assertThrows(IllegalArgumentException.class, () -> new ComissaoPlataforma(new BigDecimal(p)));
    }

    @Test
    void rejeitaValorNegativoOuNulo() {
        assertThrows(ValidacaoException.class, () -> regra.sobre(new BigDecimal("-1")));
        assertThrows(ValidacaoException.class, () -> regra.repasseAoArtista(null));
    }
}
