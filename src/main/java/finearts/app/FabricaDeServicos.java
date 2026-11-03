package finearts.app;

import finearts.infra.ConfiguracaoBanco;
import finearts.infra.MySqlConexaoFactory;
import finearts.repository.ObraRepository;
import finearts.repository.PedidoRepository;
import finearts.repository.UsuarioRepository;
import finearts.repository.jdbc.JdbcExecutor;
import finearts.repository.jdbc.JdbcObraRepository;
import finearts.repository.jdbc.JdbcPedidoRepository;
import finearts.repository.jdbc.JdbcUsuarioRepository;
import finearts.repository.memoria.InMemoryObraRepository;
import finearts.repository.memoria.InMemoryPedidoRepository;
import finearts.repository.memoria.InMemoryUsuarioRepository;
import finearts.seguranca.PasswordEncoder;
import finearts.seguranca.Pbkdf2PasswordEncoder;
import finearts.service.AutenticacaoService;
import finearts.service.CadastroService;
import finearts.service.CatalogoService;
import finearts.service.CompraService;
import finearts.service.ModeracaoService;
import finearts.service.ValidadorCadastro;
import finearts.service.ValidadorObra;

/**
 * Raiz de composição: o único lugar que conhece as implementações concretas
 * e liga repositórios aos serviços (injeção de dependência manual).
 * No projeto web, este papel passa para o contêiner (Spring).
 */
public final class FabricaDeServicos {
    private final UsuarioRepository usuarios;
    private final ObraRepository obras;
    private final PedidoRepository pedidos;
    private final PasswordEncoder encoder = new Pbkdf2PasswordEncoder();

    private FabricaDeServicos(UsuarioRepository usuarios, ObraRepository obras, PedidoRepository pedidos) {
        this.usuarios = usuarios;
        this.obras = obras;
        this.pedidos = pedidos;
    }

    public static FabricaDeServicos emMemoria() {
        InMemoryObraRepository obras = new InMemoryObraRepository();
        return new FabricaDeServicos(new InMemoryUsuarioRepository(), obras, new InMemoryPedidoRepository(obras));
    }

    public static FabricaDeServicos comMySql(ConfiguracaoBanco config) {
        JdbcExecutor sql = new JdbcExecutor(new MySqlConexaoFactory(config));
        return new FabricaDeServicos(new JdbcUsuarioRepository(sql), new JdbcObraRepository(sql),
                new JdbcPedidoRepository(sql));
    }

    public AutenticacaoService autenticacao() { return new AutenticacaoService(usuarios, encoder); }
    public CadastroService cadastro() { return new CadastroService(usuarios, encoder, new ValidadorCadastro()); }
    public ModeracaoService moderacao() { return new ModeracaoService(usuarios); }
    public CatalogoService catalogo() { return new CatalogoService(obras, new ValidadorObra()); }
    public CompraService compras() { return new CompraService(obras, pedidos); }
}
