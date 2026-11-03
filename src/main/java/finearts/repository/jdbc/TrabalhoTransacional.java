package finearts.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TrabalhoTransacional<T> {
    T executar(Connection conexao) throws SQLException;
}
