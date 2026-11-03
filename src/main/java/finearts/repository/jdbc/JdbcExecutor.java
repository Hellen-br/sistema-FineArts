package finearts.repository.jdbc;

import finearts.infra.ConexaoFactory;
import finearts.repository.RepositorioException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza o "ritual" JDBC (abrir, preparar, executar, fechar, traduzir exceções).
 * Remove o código duplicado que se repetia em cada método dos DAOs do desktop.
 */
public class JdbcExecutor {
    private final ConexaoFactory conexoes;

    public JdbcExecutor(ConexaoFactory conexoes) {
        this.conexoes = conexoes;
    }

    public <T> List<T> consultar(String sql, ParametrosSql parametros, MapeadorLinha<T> mapeador) {
        try (Connection c = conexoes.abrir()) {
            return consultar(c, sql, parametros, mapeador);
        } catch (SQLException e) {
            throw new RepositorioException("Falha ao consultar o banco de dados.", e);
        }
    }

    public int atualizar(String sql, ParametrosSql parametros) {
        try (Connection c = conexoes.abrir()) {
            return atualizar(c, sql, parametros);
        } catch (SQLException e) {
            throw new RepositorioException("Falha ao atualizar o banco de dados.", e);
        }
    }

    /** Executa várias operações numa só transação: commit se tudo der certo, rollback caso contrário. */
    public <T> T emTransacao(TrabalhoTransacional<T> trabalho) {
        try (Connection c = conexoes.abrir()) {
            c.setAutoCommit(false);
            try {
                T resultado = trabalho.executar(c);
                c.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RepositorioException("Falha na transação com o banco de dados.", e);
        }
    }

    public static <T> List<T> consultar(Connection c, String sql, ParametrosSql parametros,
                                        MapeadorLinha<T> mapeador) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            parametros.aplicar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapeador.mapear(rs));
                }
                return lista;
            }
        }
    }

    public static int atualizar(Connection c, String sql, ParametrosSql parametros) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            parametros.aplicar(ps);
            return ps.executeUpdate();
        }
    }

    /** INSERT que devolve a chave gerada (AUTO_INCREMENT). */
    public static int inserir(Connection c, String sql, ParametrosSql parametros) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            parametros.aplicar(ps);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new SQLException("O INSERT não devolveu a chave gerada.");
                }
                return chaves.getInt(1);
            }
        }
    }
}
