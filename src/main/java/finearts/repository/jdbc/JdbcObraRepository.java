package finearts.repository.jdbc;

import finearts.domain.FiltroObras;
import finearts.domain.ObraDeArte;
import finearts.repository.ObraRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcObraRepository implements ObraRepository {
    private static final String SELECT = "SELECT " + MapeadorObra.COLUNAS + " FROM " + MapeadorObra.ORIGEM;

    private final JdbcExecutor sql;

    public JdbcObraRepository(JdbcExecutor sql) {
        this.sql = sql;
    }

    @Override
    public ObraDeArte salvar(ObraDeArte o) {
        int id = sql.emTransacao(c -> JdbcExecutor.inserir(c,
                "INSERT INTO obras_de_arte (artista_id, titulo, tecnica, estilo, `local`, ano, "
                        + "largura_cm, altura_cm, preco, disponivel) VALUES (?,?,?,?,?,?,?,?,?,?)",
                ParametrosSql.de(o.getArtistaId(), o.getTitulo(), o.getTecnica(), o.getEstilo(), o.getLocal(),
                        o.getAno(), o.getDimensoes().getLarguraCm(), o.getDimensoes().getAlturaCm(),
                        o.getPreco(), o.isDisponivel())));
        o.definirId(id);
        return o;
    }

    @Override
    public Optional<ObraDeArte> buscarPorId(int id) {
        List<ObraDeArte> r = sql.consultar(SELECT + " WHERE o.id = ?", ParametrosSql.de(id), MapeadorObra::mapear);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    /** Monta o WHERE a partir do filtro; todos os valores vão como parâmetros (sem concatenar entrada do usuário). */
    @Override
    public List<ObraDeArte> pesquisar(FiltroObras f) {
        StringBuilder consulta = new StringBuilder(SELECT).append(" WHERE 1 = 1");
        List<Object> valores = new ArrayList<>();
        if (!f.isIncluirIndisponiveis()) {
            consulta.append(" AND o.disponivel = 1");
        }
        f.getArtistaId().ifPresent(v -> { consulta.append(" AND o.artista_id = ?"); valores.add(v); });
        f.getTecnica().ifPresent(v -> { consulta.append(" AND o.tecnica = ?"); valores.add(v); });
        f.getPrecoMinimo().ifPresent(v -> { consulta.append(" AND o.preco >= ?"); valores.add(v); });
        f.getPrecoMaximo().ifPresent(v -> { consulta.append(" AND o.preco <= ?"); valores.add(v); });
        f.getTermo().ifPresent(v -> {
            consulta.append(" AND (o.titulo LIKE ? OR o.tecnica LIKE ? OR o.estilo LIKE ? OR u.nome LIKE ?)");
            String padrao = "%" + escaparLike(v) + "%";
            for (int i = 0; i < 4; i++) {
                valores.add(padrao);
            }
        });
        consulta.append(" ORDER BY o.titulo");
        return sql.consultar(consulta.toString(), ParametrosSql.de(valores.toArray()), MapeadorObra::mapear);
    }

    @Override
    public void excluir(int id) {
        sql.atualizar("DELETE FROM obras_de_arte WHERE id = ?", ParametrosSql.de(id));
    }

    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
