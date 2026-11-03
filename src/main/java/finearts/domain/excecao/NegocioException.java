package finearts.domain.excecao;

/** Violação de uma regra de negócio. A camada de apresentação decide como exibi-la. */
public class NegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
