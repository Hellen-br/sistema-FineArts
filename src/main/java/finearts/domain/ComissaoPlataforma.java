package finearts.domain;

import finearts.domain.excecao.ValidacaoException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regra de cálculo isolada: comissão da plataforma sobre cada venda e valor repassado ao artista (RF014).
 * É uma classe pura (sem banco, sem tela), por isso facilmente testável com JUnit.
 */
public final class ComissaoPlataforma {
    public static final BigDecimal PERCENTUAL_PADRAO = new BigDecimal("0.15");

    private final BigDecimal percentual;

    public ComissaoPlataforma() {
        this(PERCENTUAL_PADRAO);
    }

    /** @param percentual fração entre 0 e 1 (0,15 = 15%) */
    public ComissaoPlataforma(BigDecimal percentual) {
        if (percentual == null || percentual.signum() < 0 || percentual.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("O percentual deve estar entre 0 e 1.");
        }
        this.percentual = percentual;
    }

    /** Comissão da plataforma sobre o valor da venda, arredondada para centavos (HALF_UP). */
    public BigDecimal sobre(BigDecimal valorVenda) {
        validar(valorVenda);
        return valorVenda.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }

    /** Valor que o artista recebe: venda menos a comissão. */
    public BigDecimal repasseAoArtista(BigDecimal valorVenda) {
        validar(valorVenda);
        return valorVenda.setScale(2, RoundingMode.HALF_UP).subtract(sobre(valorVenda));
    }

    public BigDecimal getPercentual() { return percentual; }

    private static void validar(BigDecimal valor) {
        if (valor == null || valor.signum() < 0) {
            throw new ValidacaoException("O valor da venda não pode ser nulo nem negativo.");
        }
    }
}
