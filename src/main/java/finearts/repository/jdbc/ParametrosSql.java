package finearts.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Preenche os "?" de um PreparedStatement. */
@FunctionalInterface
public interface ParametrosSql {
    ParametrosSql NENHUM = ps -> { };

    void aplicar(PreparedStatement ps) throws SQLException;

    static ParametrosSql de(Object... valores) {
        return ps -> {
            for (int i = 0; i < valores.length; i++) {
                ps.setObject(i + 1, valores[i]);
            }
        };
    }
}
