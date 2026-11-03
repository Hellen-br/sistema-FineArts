package finearts.seguranca;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Pbkdf2PasswordEncoder – proteção de senhas (RNF004)")
class Pbkdf2PasswordEncoderTest {
    private final PasswordEncoder encoder = new Pbkdf2PasswordEncoder(1000);

    @Test
    void hashNaoContemASenhaPura() {
        assertFalse(encoder.codificar("segredo123").contains("segredo123"));
    }

    @Test
    void confereSenhaCorreta() {
        assertTrue(encoder.confere("segredo123", encoder.codificar("segredo123")));
    }

    @Test
    void rejeitaSenhaErrada() {
        assertFalse(encoder.confere("outra", encoder.codificar("segredo123")));
    }

    @Test
    void mesmaSenhaGeraHashesDiferentesPorCausaDoSal() {
        assertNotEquals(encoder.codificar("abc123"), encoder.codificar("abc123"));
    }

    @Test
    void hashGuardaComIteracoesDiferentesAindaConfere() {
        String hash = new Pbkdf2PasswordEncoder(2000).codificar("abc123");
        assertTrue(encoder.confere("abc123", hash));   // o número de iterações vem dentro do hash
    }

    @ParameterizedTest(name = "hash inválido [{0}] é recusado")
    @ValueSource(strings = {"lixo", "PBKDF2$x$y$z", "PBKDF2$1000$@@@$@@@", "BCRYPT$1$a$b", "PBKDF2$1000$AAAA"})
    @NullAndEmptySource
    void recusaHashCorrompido(String hash) {
        assertFalse(encoder.confere("abc123", hash));
    }

    @Test
    void recusaSenhaNula() {
        assertFalse(encoder.confere(null, encoder.codificar("abc123")));
    }
}
