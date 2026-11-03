package finearts.seguranca;

/** Estratégia de proteção de senhas (OCP: trocar o algoritmo não afeta os serviços). */
public interface PasswordEncoder {
    String codificar(String senhaPura);
    boolean confere(String senhaPura, String senhaCodificada);
}
