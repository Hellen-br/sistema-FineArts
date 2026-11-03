package finearts.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConexaoFactory implements ConexaoFactory {
    private final ConfiguracaoBanco config;

    public MySqlConexaoFactory(ConfiguracaoBanco config) {
        this.config = config;
    }

    @Override
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUsuario(), config.getSenha());
    }
}
