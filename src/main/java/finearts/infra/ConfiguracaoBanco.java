package finearts.infra;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Credenciais do banco lidas de config/application.properties (fora do Git)
 * ou de variáveis de ambiente FINEARTS_DB_URL / _USER / _PASSWORD.
 * Elimina a senha "hardcoded" dentro da classe de conexão do desktop.
 */
public final class ConfiguracaoBanco {
    private final String url;
    private final String usuario;
    private final String senha;

    public ConfiguracaoBanco(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    public static ConfiguracaoBanco carregar(Path arquivo) {
        Properties p = new Properties();
        if (Files.exists(arquivo)) {
            try (InputStream in = Files.newInputStream(arquivo)) {
                p.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Não foi possível ler " + arquivo, e);
            }
        }
        String url = valor("FINEARTS_DB_URL", p.getProperty("db.url"));
        String user = valor("FINEARTS_DB_USER", p.getProperty("db.user"));
        String senha = valor("FINEARTS_DB_PASSWORD", p.getProperty("db.password"));
        if (url == null || user == null) {
            throw new IllegalStateException("Configuração do banco ausente. Copie config/application.properties.example "
                    + "para config/application.properties e preencha, ou defina as variáveis FINEARTS_DB_*.");
        }
        return new ConfiguracaoBanco(url, user, senha == null ? "" : senha);
    }

    private static String valor(String variavelAmbiente, String propriedade) {
        String env = System.getenv(variavelAmbiente);
        return env != null && !env.isEmpty() ? env : propriedade;
    }

    public String getUrl() { return url; }
    public String getUsuario() { return usuario; }
    public String getSenha() { return senha; }

    @Override
    public String toString() { return "ConfiguracaoBanco{url=" + url + ", usuario=" + usuario + ", senha=****}"; }
}
