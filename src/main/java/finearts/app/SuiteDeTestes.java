package finearts.app;

import java.util.ArrayList;
import java.util.List;

/** Minirrunner de testes para o main() (sem dependência de JUnit). */
public class SuiteDeTestes {
    private int aprovados;
    private final List<String> falhas = new ArrayList<>();

    public void teste(String nome, Runnable corpo) {
        try {
            corpo.run();
            aprovados++;
            System.out.println("  [OK]    " + nome);
        } catch (AssertionError | RuntimeException e) {
            falhas.add(nome + " -> " + e);
            System.out.println("  [FALHA] " + nome + " -> " + e);
        }
    }

    public static void afirmar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    public static void igual(Object esperado, Object atual, String mensagem) {
        if (esperado == null ? atual != null : !esperado.equals(atual)) {
            throw new AssertionError(mensagem + " (esperado=" + esperado + ", atual=" + atual + ")");
        }
    }

    public static <E extends RuntimeException> E lanca(Class<E> tipo, Runnable acao, String mensagem) {
        try {
            acao.run();
        } catch (RuntimeException e) {
            if (tipo.isInstance(e)) {
                return tipo.cast(e);
            }
            throw new AssertionError(mensagem + " (lançou " + e.getClass().getSimpleName() + ")");
        }
        throw new AssertionError(mensagem + " (nenhuma exceção lançada)");
    }

    public void titulo(String texto) { System.out.println("\n== " + texto + " =="); }
    public boolean houveFalhas() { return !falhas.isEmpty(); }

    public void resumo() {
        System.out.println("\n----------------------------------------");
        System.out.println("Aprovados: " + aprovados + " | Falhas: " + falhas.size());
        falhas.forEach(f -> System.out.println("  - " + f));
    }
}
