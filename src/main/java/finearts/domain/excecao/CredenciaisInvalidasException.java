package finearts.domain.excecao;

public class CredenciaisInvalidasException extends NegocioException {
    private static final long serialVersionUID = 1L;

    public CredenciaisInvalidasException() {
        // Mensagem única: não revela se o e-mail existe (evita enumeração de usuários).
        super("E-mail ou senha incorretos.");
    }
}
