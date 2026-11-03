package finearts.domain;

import finearts.domain.excecao.ValidacaoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Divisão de um valor em parcelas (cartão de crédito, RF009/RF016).
 * Regras: no máximo 12 parcelas e parcela mínima de R$ 100,00 (exceto à vista).
 * A soma das parcelas é sempre igual ao total: a diferença de centavos fica na primeira parcela.
 */
public final class Parcelamento {
    public static final int MAXIMO_PARCELAS = 12;
    public static final BigDecimal VALOR_MINIMO_PARCELA = new BigDecimal("100.00");

    private Parcelamento() { }

    public static List<BigDecimal> dividir(BigDecimal total, int parcelas) {
        if (total == null || total.signum() <= 0) {
            throw new ValidacaoException("O valor a parcelar deve ser maior que zero.");
        }
        if (parcelas < 1 || parcelas > MAXIMO_PARCELAS) {
            throw new ValidacaoException("O número de parcelas deve estar entre 1 e " + MAXIMO_PARCELAS + ".");
        }
        BigDecimal valor = total.setScale(2, RoundingMode.HALF_UP);
        BigDecimal quantidade = BigDecimal.valueOf(parcelas);
        BigDecimal base = valor.divide(quantidade, 2, RoundingMode.DOWN);
        if (parcelas > 1 && base.compareTo(VALOR_MINIMO_PARCELA) < 0) {
            throw new ValidacaoException("Cada parcela deve ter no mínimo R$ 100,00.");
        }
        BigDecimal resto = valor.subtract(base.multiply(quantidade));
        List<BigDecimal> lista = new ArrayList<>();
        lista.add(base.add(resto));
        for (int i = 1; i < parcelas; i++) {
            lista.add(base);
        }
        return lista;
    }

    /** Maior número de parcelas permitido para o valor informado (mínimo 1). */
    public static int maximoPermitido(BigDecimal total) {
        if (total == null || total.signum() <= 0) {
            return 1;
        }
        int porValor = total.divideToIntegralValue(VALOR_MINIMO_PARCELA).intValue();
        return Math.max(1, Math.min(MAXIMO_PARCELAS, porValor));
    }
}
