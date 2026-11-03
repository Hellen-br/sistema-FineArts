package finearts.domain.excecao;

public class ObraIndisponivelException extends NegocioException {
    private static final long serialVersionUID = 1L;

    public ObraIndisponivelException(String tituloObra) {
        super("A obra \"" + tituloObra + "\" não está mais disponível.");
    }
}
