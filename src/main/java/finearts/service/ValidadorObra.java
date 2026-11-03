package finearts.service;

import finearts.domain.excecao.ValidacaoException;
import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class ValidadorObra {
    public void validar(DadosObra d) {
        List<String> erros = new ArrayList<>();
        if (vazio(d.getTitulo())) erros.add("Informe o título da obra.");
        if (vazio(d.getTecnica())) erros.add("Informe a técnica.");
        if (d.getAno() < 1000 || d.getAno() > Year.now().getValue()) erros.add("Ano inválido.");
        if (d.getLarguraCm() <= 0 || d.getAlturaCm() <= 0) erros.add("Informe largura e altura positivas.");
        if (d.getPreco() == null || d.getPreco().compareTo(BigDecimal.ZERO) <= 0) erros.add("Preço inválido.");
        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }
    }

    private static boolean vazio(String s) { return s == null || s.trim().isEmpty(); }
}
