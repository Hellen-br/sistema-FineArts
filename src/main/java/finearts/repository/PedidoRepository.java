package finearts.repository;

import finearts.domain.Certificado;
import finearts.domain.Pedido;
import finearts.domain.StatusPedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    /** Grava pedido, itens e certificados e marca as obras como vendidas, de forma atômica. */
    Pedido salvar(Pedido pedido);
    Optional<Pedido> buscarPorId(int id);
    List<Pedido> listarPorCliente(int clienteId);
    List<Certificado> listarCertificadosDoCliente(int clienteId);
    void atualizarStatus(int pedidoId, StatusPedido novoStatus);
}
