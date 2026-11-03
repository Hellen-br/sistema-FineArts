package finearts.repository.jdbc;

import finearts.domain.Dimensoes;
import finearts.domain.ObraDeArte;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Mapeamento único de obra; as consultas de obras, pedidos e certificados reutilizam-no. */
final class MapeadorObra {
    static final String COLUNAS =
            "o.id AS obra_id, o.artista_id, u.nome AS nome_artista, o.titulo, o.tecnica, o.estilo, "
            + "o.`local` AS local_obra, o.ano, o.largura_cm, o.altura_cm, o.preco, o.disponivel";

    static final String ORIGEM =
            "obras_de_arte o JOIN usuarios u ON u.id = o.artista_id";

    private MapeadorObra() { }

    static ObraDeArte mapear(ResultSet rs) throws SQLException {
        return ObraDeArte.builder()
                .id(rs.getInt("obra_id"))
                .artistaId(rs.getInt("artista_id"))
                .nomeArtista(rs.getString("nome_artista"))
                .titulo(rs.getString("titulo"))
                .tecnica(rs.getString("tecnica"))
                .estilo(rs.getString("estilo"))
                .local(rs.getString("local_obra"))
                .ano(rs.getInt("ano"))
                .dimensoes(new Dimensoes(rs.getDouble("largura_cm"), rs.getDouble("altura_cm")))
                .preco(rs.getBigDecimal("preco"))
                .disponivel(rs.getBoolean("disponivel"))
                .build();
    }
}
