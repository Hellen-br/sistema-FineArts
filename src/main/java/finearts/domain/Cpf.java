package finearts.domain;

/** Validação e formatação de CPF (algoritmo dos dígitos verificadores). */
public final class Cpf {
    private Cpf() { }

    public static boolean valido(String texto) {
        String d = somenteDigitos(texto);
        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            return false;
        }
        return digito(d, 9) == d.charAt(9) - '0' && digito(d, 10) == d.charAt(10) - '0';
    }

    /** Retorna o CPF no formato 000.000.000-00. Pressupõe CPF válido. */
    public static String formatar(String texto) {
        String d = somenteDigitos(texto);
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    private static int digito(String d, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (d.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }
}
