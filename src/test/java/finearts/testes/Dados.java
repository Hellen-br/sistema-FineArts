package finearts.testes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import finearts.app.FabricaDeServicos;
import finearts.domain.Administrador;
import finearts.domain.Artista;
import finearts.domain.Dimensoes;
import finearts.domain.ObraDeArte;
import finearts.service.DadosCadastro;
import finearts.service.DadosObra;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Fábrica de dados de teste compartilhada pelas classes de teste. */
public final class Dados {
    public static final String CPF_1 = "529.982.247-25";
    public static final String CPF_2 = "168.995.350-09";
    public static final String CPF_3 = "111.444.777-35";
    public static final String SENHA = "123456";

    private Dados() { }

    public static DadosCadastro cadastro(String nome, String cpf, String email) {
        return new DadosCadastro().nome(nome).cpf(cpf).email(email).senha(SENHA).confirmacaoSenha(SENHA)
                .telefone("(62) 99999-0000");
    }

    public static DadosObra dadosObra(String titulo, String tecnica, String preco) {
        return new DadosObra().titulo(titulo).tecnica(tecnica).estilo("Realismo").local("Goiânia").ano(2023)
                .larguraCm(80).alturaCm(60).preco(new BigDecimal(preco));
    }

    public static ObraDeArte obra(int id, String titulo, String tecnica, String preco) {
        return ObraDeArte.builder().id(id).artistaId(1).nomeArtista("Ana Lima").titulo(titulo).tecnica(tecnica)
                .estilo("Realismo").local("Cuiabá").ano(2023).dimensoes(new Dimensoes(80, 60))
                .preco(new BigDecimal(preco)).build();
    }

    public static Administrador admin() {
        return new Administrador(1, "Admin", CPF_3, "admin@finearts.com", "x", LocalDate.now());
    }

    public static Artista artistaAprovada(FabricaDeServicos f, String email, String cpf) {
        Artista a = f.cadastro().cadastrarArtista(cadastro("Artista " + email, cpf, email));
        f.moderacao().aprovarArtista(admin(), a.getId());
        return (Artista) f.autenticacao().autenticar(email, SENHA);
    }

    /** Compara valores monetários ignorando a escala (1000 == 1000.00). */
    public static void assertValor(String esperado, BigDecimal atual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(atual),
                "esperado " + esperado + " mas foi " + atual);
    }
}
