package finearts.service;

import static finearts.testes.Dados.CPF_1;
import static finearts.testes.Dados.cadastro;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import finearts.domain.excecao.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("ValidadorCadastro – regras de RF001 / RF004")
class ValidadorCadastroTest {
    private final ValidadorCadastro validador = new ValidadorCadastro();

    @Test
    void aceitaDadosValidos() {
        assertDoesNotThrow(() -> validador.validar(cadastro("Maria", CPF_1, "maria@email.com")));
    }

    @Test
    void reuneTodosOsErros() {
        ValidacaoException e = assertThrows(ValidacaoException.class, () -> validador.validar(
                new DadosCadastro().nome(" ").cpf("1").email("x").senha("123").confirmacaoSenha("123")));
        assertEquals(4, e.getErros().size());
    }

    @ParameterizedTest(name = "e-mail inválido: [{0}]")
    @ValueSource(strings = {"", "semarroba", "a@b", "a b@c.com", "@c.com"})
    void rejeitaEmailInvalido(String email) {
        ValidacaoException e = assertThrows(ValidacaoException.class,
                () -> validador.validar(cadastro("Maria", CPF_1, email)));
        assertTrue(e.getErros().contains("E-mail inválido."));
    }

    @ParameterizedTest(name = "senha curta: [{0}]")
    @ValueSource(strings = {"", "1", "12345"})
    void rejeitaSenhaComMenosDeSeisCaracteres(String senha) {
        ValidacaoException e = assertThrows(ValidacaoException.class,
                () -> validador.validar(cadastro("Maria", CPF_1, "m@e.com").senha(senha).confirmacaoSenha(senha)));
        assertEquals(1, e.getErros().size());
    }

    @Test
    void rejeitaConfirmacaoDiferente() {
        ValidacaoException e = assertThrows(ValidacaoException.class,
                () -> validador.validar(cadastro("Maria", CPF_1, "m@e.com").confirmacaoSenha("outra123")));
        assertTrue(e.getErros().contains("As senhas não coincidem."));
    }

    @Test
    void rejeitaCpfInvalidoENomeVazio() {
        ValidacaoException e = assertThrows(ValidacaoException.class,
                () -> validador.validar(cadastro("", "111.111.111-11", "m@e.com")));
        assertEquals(2, e.getErros().size());
    }
}
