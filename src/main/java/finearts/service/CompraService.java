package finearts.service;

import finearts.domain.Carrinho;
import finearts.domain.Certificado;
import finearts.domain.Cliente;
import finearts.domain.Parcelamento;
import finearts.domain.FormaPagamento;
import finearts.domain.ObraDeArte;
import finearts.domain.Pedido;
import finearts.domain.StatusPedido;
import finearts.domain.excecao.ObraIndisponivelException;
import finearts.domain.excecao.ValidacaoException;
import finearts.repository.ObraRepository;
import finearts.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** RF010/RF011/RF012: finalizar compra, certificados e acompanhamento de pedidos. */
public class CompraService {
    private final ObraRepository obras;
    private final PedidoRepository pedidos;

    public CompraService(ObraRepository obras, PedidoRepository pedidos) {
        this.obras = obras;
        this.pedidos = pedidos;
    }

    public Pedido finalizarCompra(Cliente cliente, Carrinho carrinho, FormaPagamento forma) {
        if (carrinho.isVazio()) {
            throw new ValidacaoException("O carrinho está vazio.");
        }
        if (forma == null) {
            throw new ValidacaoException("Selecione a forma de pagamento.");
        }
        // Relê cada obra: outra pessoa pode ter comprado enquanto ela estava no carrinho.
        List<ObraDeArte> atuais = new ArrayList<>();
        for (ObraDeArte noCarrinho : carrinho.getItens()) {
            ObraDeArte atual = obras.buscarPorId(noCarrinho.getId())
                    .orElseThrow(() -> new ObraIndisponivelException(noCarrinho.getTitulo()));
            if (!atual.isDisponivel()) {
                throw new ObraIndisponivelException(atual.getTitulo());
            }
            atuais.add(atual);
        }
        Pedido pedido = pedidos.salvar(Pedido.novo(cliente.getId(), atuais, forma));
        carrinho.limpar();
        return pedido;
    }

    /** Simula o parcelamento do carrinho no cartão de crédito (não grava nada). */
    public List<BigDecimal> simularParcelamento(Carrinho carrinho, int parcelas) {
        if (carrinho.isVazio()) {
            throw new ValidacaoException("O carrinho está vazio.");
        }
        return Parcelamento.dividir(carrinho.total(), parcelas);
    }

    public List<Pedido> listarPedidos(Cliente cliente) {
        return pedidos.listarPorCliente(cliente.getId());
    }

    public List<Certificado> listarCertificados(Cliente cliente) {
        return pedidos.listarCertificadosDoCliente(cliente.getId());
    }

    public void atualizarStatus(int pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidos.buscarPorId(pedidoId)
                .orElseThrow(() -> new ValidacaoException("Pedido não encontrado."));
        pedido.atualizarStatus(novoStatus); // valida a transição
        pedidos.atualizarStatus(pedidoId, novoStatus);
    }
}
