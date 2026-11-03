package finearts.domain;

import finearts.domain.excecao.NegocioException;
import finearts.domain.excecao.ValidacaoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pedido {
    private int id;
    private final int clienteId;
    private final FormaPagamento formaPagamento;
    private StatusPedido status;
    private final LocalDateTime dataHora;
    private final List<ItemPedido> itens;
    private final List<Certificado> certificados = new ArrayList<>();

    private Pedido(int id, int clienteId, FormaPagamento forma, StatusPedido status,
                   LocalDateTime dataHora, List<ItemPedido> itens) {
        this.id = id;
        this.clienteId = clienteId;
        this.formaPagamento = forma;
        this.status = status;
        this.dataHora = dataHora;
        this.itens = itens;
    }

    /**
     * Cria o pedido de uma compra. Não há gateway de pagamento no escopo do PI,
     * então o pedido nasce PAGO e já emite um certificado por obra.
     */
    public static Pedido novo(int clienteId, Collection<ObraDeArte> obras, FormaPagamento forma) {
        if (obras == null || obras.isEmpty()) {
            throw new ValidacaoException("O pedido precisa de ao menos uma obra.");
        }
        List<ItemPedido> itens = new ArrayList<>();
        for (ObraDeArte o : obras) {
            itens.add(new ItemPedido(o, o.getPreco()));
        }
        Pedido p = new Pedido(0, clienteId, forma, StatusPedido.PAGO, LocalDateTime.now(), itens);
        for (ObraDeArte o : obras) {
            p.certificados.add(Certificado.emitir(o, clienteId));
        }
        return p;
    }

    /** Reconstitui um pedido já gravado (usado pelos repositórios). */
    public static Pedido reconstituir(int id, int clienteId, FormaPagamento forma, StatusPedido status,
                                      LocalDateTime dataHora, List<ItemPedido> itens) {
        return new Pedido(id, clienteId, forma, status, dataHora, new ArrayList<>(itens));
    }

    public void atualizarStatus(StatusPedido novo) {
        if (!status.podeIrPara(novo)) {
            throw new NegocioException("Transição inválida de pedido: " + status + " -> " + novo);
        }
        this.status = novo;
    }

    public void definirId(int novoId) {
        if (id != 0) {
            throw new IllegalStateException("Identificador já definido.");
        }
        this.id = novoId;
    }

    public BigDecimal getValorTotal() {
        BigDecimal soma = BigDecimal.ZERO;
        for (ItemPedido i : itens) {
            soma = soma.add(i.getPrecoUnitario());
        }
        return soma;
    }

    /** Comissão total da plataforma neste pedido (soma por item, cada uma arredondada em centavos). */
    public BigDecimal getComissaoPlataforma() {
        ComissaoPlataforma regra = new ComissaoPlataforma();
        BigDecimal soma = BigDecimal.ZERO;
        for (ItemPedido i : itens) {
            soma = soma.add(regra.sobre(i.getPrecoUnitario()));
        }
        return soma;
    }

    /** Total a repassar aos artistas neste pedido. */
    public BigDecimal getRepasseAosArtistas() {
        return getValorTotal().subtract(getComissaoPlataforma());
    }

    public int getId() { return id; }
    public int getClienteId() { return clienteId; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public StatusPedido getStatus() { return status; }
    public LocalDateTime getDataHora() { return dataHora; }
    public List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }
    public List<Certificado> getCertificados() { return Collections.unmodifiableList(certificados); }
}
