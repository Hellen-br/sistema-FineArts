package finearts.domain;

import finearts.domain.excecao.ObraIndisponivelException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carrinho como objeto de domínio. No desktop a lista vivia dentro do JPanel
 * (regra de negócio presa à interface); no web ficará guardado na sessão.
 */
public class Carrinho {
    private final Map<Integer, ObraDeArte> itens = new LinkedHashMap<>();

    /** @return true se a obra foi adicionada; false se já estava no carrinho. */
    public boolean adicionar(ObraDeArte obra) {
        if (!obra.isDisponivel()) {
            throw new ObraIndisponivelException(obra.getTitulo());
        }
        return itens.putIfAbsent(obra.getId(), obra) == null;
    }

    public boolean remover(int obraId) { return itens.remove(obraId) != null; }
    public void limpar() { itens.clear(); }
    public boolean isVazio() { return itens.isEmpty(); }
    public int quantidade() { return itens.size(); }

    public List<ObraDeArte> getItens() {
        return Collections.unmodifiableList(new ArrayList<>(itens.values()));
    }

    public BigDecimal total() {
        BigDecimal soma = BigDecimal.ZERO;
        for (ObraDeArte o : itens.values()) {
            soma = soma.add(o.getPreco());
        }
        return soma;
    }
}
