package finearts.repository.jdbc;

import finearts.domain.Administrador;
import finearts.domain.Artista;
import finearts.domain.Cliente;
import finearts.domain.PerfilCliente;
import finearts.domain.TipoUsuario;
import finearts.domain.Usuario;
import finearts.repository.UsuarioRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class JdbcUsuarioRepository implements UsuarioRepository {
    private static final String SELECT =
            "SELECT u.id, u.nome, u.cpf, u.email, u.senha, u.tipo, u.data_cadastro, "
            + "a.telefone AS art_telefone, a.formacao, a.redes_sociais, a.aprovado, "
            + "c.telefone AS cli_telefone, c.tipo_cliente "
            + "FROM usuarios u "
            + "LEFT JOIN artistas a ON a.usuario_id = u.id "
            + "LEFT JOIN clientes c ON c.usuario_id = u.id ";

    private final JdbcExecutor sql;

    public JdbcUsuarioRepository(JdbcExecutor sql) {
        this.sql = sql;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        List<Usuario> r = sql.consultar(SELECT + "WHERE u.email = ?", ParametrosSql.de(email), this::mapear);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override
    public boolean existePorEmail(String email) {
        return !sql.consultar("SELECT 1 FROM usuarios WHERE email = ?", ParametrosSql.de(email), rs -> 1).isEmpty();
    }

    @Override
    public boolean existePorCpf(String cpf) {
        return !sql.consultar("SELECT 1 FROM usuarios WHERE cpf = ?", ParametrosSql.de(cpf), rs -> 1).isEmpty();
    }

    @Override
    public Artista salvar(Artista a) {
        sql.emTransacao(c -> {
            a.definirId(inserirUsuario(c, a));
            JdbcExecutor.atualizar(c,
                    "INSERT INTO artistas (usuario_id, telefone, formacao, redes_sociais, aprovado) VALUES (?,?,?,?,?)",
                    ParametrosSql.de(a.getId(), a.getTelefone(), a.getFormacao(), a.getRedesSociais(), a.isAprovado()));
            return null;
        });
        return a;
    }

    @Override
    public Cliente salvar(Cliente cl) {
        sql.emTransacao(c -> {
            cl.definirId(inserirUsuario(c, cl));
            JdbcExecutor.atualizar(c,
                    "INSERT INTO clientes (usuario_id, telefone, tipo_cliente) VALUES (?,?,?)",
                    ParametrosSql.de(cl.getId(), cl.getTelefone(),
                            cl.getPerfil() == null ? null : cl.getPerfil().paraBanco()));
            return null;
        });
        return cl;
    }

    @Override
    public boolean aprovarArtista(int artistaId) {
        return sql.atualizar("UPDATE artistas SET aprovado = 1 WHERE usuario_id = ?", ParametrosSql.de(artistaId)) > 0;
    }

    @Override
    public List<Artista> listarArtistasPendentes() {
        List<Usuario> lista = sql.consultar(SELECT + "WHERE u.tipo = 'ARTISTA' AND a.aprovado = 0 ORDER BY u.nome",
                ParametrosSql.NENHUM, this::mapear);
        List<Artista> artistas = new java.util.ArrayList<>();
        for (Usuario u : lista) {
            artistas.add((Artista) u);
        }
        return artistas;
    }

    private int inserirUsuario(java.sql.Connection c, Usuario u) throws SQLException {
        return JdbcExecutor.inserir(c,
                "INSERT INTO usuarios (nome, cpf, email, senha, tipo, data_cadastro) VALUES (?,?,?,?,?,?)",
                ParametrosSql.de(u.getNome(), u.getCpf(), u.getEmail(), u.getSenhaHash(),
                        u.getTipo().name(), u.getDataCadastro()));
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String cpf = rs.getString("cpf");
        String email = rs.getString("email");
        String senha = rs.getString("senha");
        LocalDate cadastro = rs.getObject("data_cadastro", LocalDate.class);
        TipoUsuario tipo = TipoUsuario.valueOf(rs.getString("tipo"));
        switch (tipo) {
            case ARTISTA:
                Artista a = new Artista(id, nome, cpf, email, senha, cadastro);
                a.setTelefone(rs.getString("art_telefone"));
                a.setFormacao(rs.getString("formacao"));
                a.setRedesSociais(rs.getString("redes_sociais"));
                if (rs.getBoolean("aprovado")) {
                    a.aprovar();
                }
                return a;
            case CLIENTE:
                Cliente c = new Cliente(id, nome, cpf, email, senha, cadastro);
                c.setTelefone(rs.getString("cli_telefone"));
                c.setPerfil(PerfilCliente.deBanco(rs.getString("tipo_cliente")));
                return c;
            default:
                return new Administrador(id, nome, cpf, email, senha, cadastro);
        }
    }
}
