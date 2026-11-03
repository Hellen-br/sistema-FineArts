package finearts.infra;

import java.sql.Connection;
import java.sql.SQLException;

/** Abstração de "como obter uma conexão" (DIP). Cada operação abre e fecha a sua. */
public interface ConexaoFactory {
    Connection abrir() throws SQLException;
}
