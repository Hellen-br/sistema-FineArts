package finearts.service;

import finearts.domain.Cpf;
import finearts.domain.excecao.ValidacaoException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Única responsabilidade: validar o formato dos dados de cadastro. */
public class ValidadorCadastro {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    static final int TAMANHO_MINIMO_SENHA = 6;

    public void validar(DadosCadastro d) {
        List<String> erros = new ArrayList<>();
        if (vazio(d.getNome())) erros.add("Informe o nome.");
        if (!Cpf.valido(d.getCpf())) erros.add("CPF inválido.");
        if (vazio(d.getEmail()) || !EMAIL.matcher(d.getEmail().trim()).matches()) erros.add("E-mail inválido.");
        if (d.getSenha() == null || d.getSenha().length() < TAMANHO_MINIMO_SENHA) {
            erros.add("A senha deve ter ao menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        } else if (!d.getSenha().equals(d.getConfirmacaoSenha())) {
            erros.add("As senhas não coincidem.");
        }
        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }
    }

    private static boolean vazio(String s) { return s == null || s.trim().isEmpty(); }
}
