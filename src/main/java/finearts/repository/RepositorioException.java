package finearts.repository;

/** Falha técnica de persistência (não é regra de negócio). Encapsula SQLException. */
public class RepositorioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RepositorioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
