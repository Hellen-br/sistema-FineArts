package finearts.repository.memoria;

import finearts.domain.Certificado;
import finearts.domain.ItemPedido;
import finearts.domain.ObraDeArte;
import finearts.domain.Pedido;
import finearts.domain.StatusPedido;
import finearts.domain.excecao.ObraIndisponivelException;
import finearts.repository.PedidoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryPedidoRepository implements PedidoRepository {
    private final InMemoryObraRepository obras;
    private final List<Pedido> pedidos = new ArrayList<>();
    private final List<Certificado> certificados = new ArrayList<>();
    private int sequencia = 0;

    public InMemoryPedidoRepository(InMemoryObraRepository obras) {
        this.obras = obras;
    }

    @Override
    public Pedido salvar(Pedido pedido) {
        // Simula a atomicidade do banco: valida tudo antes de alterar qualquer coisa.
        List<ObraDeArte> armazenadas = new ArrayList<>();
        for (ItemPedido item : pedido.getItens()) {
            ObraDeArte atual = obras.buscarPorId(item.getObra().getId())
                    .orElseThrow(() -> new ObraIndisponivelException(item.getObra().getTitulo()));
            if (!atual.isDisponivel()) {
                throw new ObraIndisponivelException(atual.getTitulo());
            }
            armazenadas.add(atual);
        }
        armazenadas.forEach(ObraDeArte::marcarComoVendida);
        pedido.definirId(++sequencia);
        pedidos.add(pedido);
        certificados.addAll(pedido.getCertificados());
        return pedido;
    }

    @Override
    public Optional<Pedido> buscarPorId(int id) {
        return pedidos.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public List<Pedido> listarPorCliente(int clienteId) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getClienteId() == clienteId) {
                resultado.add(0, p); // mais recente primeiro
            }
        }
        return resultado;
    }

    @Override
    public List<Certificado> listarCertificadosDoCliente(int clienteId) {
        List<Certificado> resultado = new ArrayList<>();
        for (Certificado c : certificados) {
            if (c.getCompradorId() == clienteId) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public void atualizarStatus(int pedidoId, StatusPedido novoStatus) {
        // Em memória o objeto já foi alterado pelo serviço; nada a persistir.
    }
}
