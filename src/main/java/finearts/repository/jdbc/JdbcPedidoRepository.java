package finearts.repository.jdbc;

import finearts.domain.Certificado;
import finearts.domain.FormaPagamento;
import finearts.domain.ItemPedido;
import finearts.domain.ObraDeArte;
import finearts.domain.Pedido;
import finearts.domain.StatusPedido;
import finearts.domain.excecao.ObraIndisponivelException;
import finearts.repository.PedidoRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcPedidoRepository implements PedidoRepository {
    private static final String SELECT_PEDIDOS =
            "SELECT p.id AS pedido_id, p.cliente_id, p.forma_pagamento, p.status, p.data_hora, "
            + "i.preco_unitario, " + MapeadorObra.COLUNAS
            + " FROM pedidos p JOIN itens_pedido i ON i.pedido_id = p.id "
            + "JOIN obras_de_arte o ON o.id = i.obra_id JOIN usuarios u ON u.id = o.artista_id ";

    private final JdbcExecutor sql;

    public JdbcPedidoRepository(JdbcExecutor sql) {
        this.sql = sql;
    }

    @Override
    public Pedido salvar(Pedido pedido) {
        sql.emTransacao(c -> {
            int pedidoId = JdbcExecutor.inserir(c,
                    "INSERT INTO pedidos (cliente_id, valor_total, forma_pagamento, status, data_hora) VALUES (?,?,?,?,?)",
                    ParametrosSql.de(pedido.getClienteId(), pedido.getValorTotal(),
                            pedido.getFormaPagamento().name(), pedido.getStatus().name(), pedido.getDataHora()));
            for (ItemPedido item : pedido.getItens()) {
                registrarItem(c, pedidoId, item);
            }
            for (Certificado cert : pedido.getCertificados()) {
                JdbcExecutor.atualizar(c,
                        "INSERT INTO certificados (codigo, obra_id, pedido_id, comprador_id, data_emissao) VALUES (?,?,?,?,?)",
                        ParametrosSql.de(cert.getCodigo(), cert.getObra().getId(), pedidoId,
                                cert.getCompradorId(), cert.getDataEmissao()));
            }
            pedido.definirId(pedidoId);
            return null;
        });
        return pedido;
    }

    /** O "AND disponivel = 1" garante que duas compras simultâneas não vendam a mesma obra. */
    private void registrarItem(java.sql.Connection c, int pedidoId, ItemPedido item) throws SQLException {
        int vendidas = JdbcExecutor.atualizar(c,
                "UPDATE obras_de_arte SET disponivel = 0 WHERE id = ? AND disponivel = 1",
                ParametrosSql.de(item.getObra().getId()));
        if (vendidas == 0) {
            throw new ObraIndisponivelException(item.getObra().getTitulo());
        }
        JdbcExecutor.atualizar(c,
                "INSERT INTO itens_pedido (pedido_id, obra_id, preco_unitario) VALUES (?,?,?)",
                ParametrosSql.de(pedidoId, item.getObra().getId(), item.getPrecoUnitario()));
    }

    @Override
    public Optional<Pedido> buscarPorId(int id) {
        List<Pedido> r = consultarPedidos("WHERE p.id = ?", id);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override
    public List<Pedido> listarPorCliente(int clienteId) {
        return consultarPedidos("WHERE p.cliente_id = ?", clienteId);
    }

    @Override
    public List<Certificado> listarCertificadosDoCliente(int clienteId) {
        String consulta = "SELECT c.codigo, c.data_emissao, c.comprador_id, " + MapeadorObra.COLUNAS
                + " FROM certificados c JOIN obras_de_arte o ON o.id = c.obra_id "
                + "JOIN usuarios u ON u.id = o.artista_id WHERE c.comprador_id = ? ORDER BY c.id";
        return sql.consultar(consulta, ParametrosSql.de(clienteId), rs -> new Certificado(
                rs.getString("codigo"), MapeadorObra.mapear(rs), rs.getInt("comprador_id"),
                rs.getObject("data_emissao", LocalDate.class)));
    }

    @Override
    public void atualizarStatus(int pedidoId, StatusPedido novoStatus) {
        sql.atualizar("UPDATE pedidos SET status = ? WHERE id = ?", ParametrosSql.de(novoStatus.name(), pedidoId));
    }

    /** Uma única consulta com JOIN (evita o problema N+1) e agrupamento em Java. */
    private List<Pedido> consultarPedidos(String where, int parametro) {
        List<LinhaPedido> linhas = sql.consultar(SELECT_PEDIDOS + where + " ORDER BY p.id DESC, o.titulo",
                ParametrosSql.de(parametro), rs -> new LinhaPedido(
                        rs.getInt("pedido_id"), rs.getInt("cliente_id"),
                        FormaPagamento.valueOf(rs.getString("forma_pagamento")),
                        StatusPedido.valueOf(rs.getString("status")),
                        rs.getObject("data_hora", LocalDateTime.class),
                        new ItemPedido(MapeadorObra.mapear(rs), rs.getBigDecimal("preco_unitario"))));
        Map<Integer, List<LinhaPedido>> agrupadas = new LinkedHashMap<>();
        for (LinhaPedido l : linhas) {
            agrupadas.computeIfAbsent(l.id, k -> new ArrayList<>()).add(l);
        }
        List<Pedido> pedidos = new ArrayList<>();
        for (List<LinhaPedido> grupo : agrupadas.values()) {
            LinhaPedido primeira = grupo.get(0);
            List<ItemPedido> itens = new ArrayList<>();
            for (LinhaPedido l : grupo) {
                itens.add(l.item);
            }
            pedidos.add(Pedido.reconstituir(primeira.id, primeira.clienteId, primeira.forma,
                    primeira.status, primeira.dataHora, itens));
        }
        return pedidos;
    }

    private static final class LinhaPedido {
        final int id;
        final int clienteId;
        final FormaPagamento forma;
        final StatusPedido status;
        final LocalDateTime dataHora;
        final ItemPedido item;

        LinhaPedido(int id, int clienteId, FormaPagamento forma, StatusPedido status,
                    LocalDateTime dataHora, ItemPedido item) {
            this.id = id;
            this.clienteId = clienteId;
            this.forma = forma;
            this.status = status;
            this.dataHora = dataHora;
            this.item = item;
        }
    }
}
