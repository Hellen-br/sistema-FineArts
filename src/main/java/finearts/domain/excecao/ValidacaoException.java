package finearts.domain.excecao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Dados de entrada inválidos. Guarda todos os erros de uma vez (útil para formulários web). */
public class ValidacaoException extends NegocioException {
    private static final long serialVersionUID = 1L;
    private final ArrayList<String> erros;

    public ValidacaoException(String erro) {
        super(erro);
        this.erros = new ArrayList<>(Collections.singletonList(erro));
    }

    public ValidacaoException(List<String> erros) {
        super(String.join(" ", erros));
        this.erros = new ArrayList<>(erros);
    }

    public List<String> getErros() {
        return Collections.unmodifiableList(erros);
    }
}
