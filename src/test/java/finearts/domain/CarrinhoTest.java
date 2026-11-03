package finearts.domain;

import static finearts.testes.Dados.assertValor;
import static finearts.testes.Dados.obra;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import finearts.domain.excecao.ObraIndisponivelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Carrinho – itens e total (RF009)")
class CarrinhoTest {
    private Carrinho carrinho;

    @BeforeEach
    void criarCarrinho() {
        carrinho = new Carrinho();
    }

    @Test
    void carrinhoNovoEstaVazioComTotalZero() {
        assertTrue(carrinho.isVazio());
        assertEquals(0, carrinho.quantidade());
        assertValor("0", carrinho.total());
    }

    @Test
    void somaOsPrecosDasObras() {
        carrinho.adicionar(obra(1, "A", "Óleo", "100.50"));
        carrinho.adicionar(obra(2, "B", "Aquarela", "200.25"));
        assertEquals(2, carrinho.quantidade());
        assertValor("300.75", carrinho.total());
    }

    @Test
    void naoPermiteObraDuplicada() {
        ObraDeArte o = obra(1, "A", "Óleo", "100");
        assertTrue(carrinho.adicionar(o));
        assertFalse(carrinho.adicionar(o));
        assertEquals(1, carrinho.quantidade());
        assertValor("100", carrinho.total());
    }

    @Test
    void recusaObraJaVendida() {
        ObraDeArte o = obra(1, "A", "Óleo", "100");
        o.marcarComoVendida();
        assertThrows(ObraIndisponivelException.class, () -> carrinho.adicionar(o));
        assertTrue(carrinho.isVazio());
    }

    @Test
    void removeObraPeloId() {
        carrinho.adicionar(obra(1, "A", "Óleo", "100"));
        carrinho.adicionar(obra(2, "B", "Óleo", "50"));
        assertTrue(carrinho.remover(1));
        assertFalse(carrinho.remover(1));
        assertValor("50", carrinho.total());
    }

    @Test
    void limparEsvaziaOCarrinho() {
        carrinho.adicionar(obra(1, "A", "Óleo", "100"));
        carrinho.limpar();
        assertTrue(carrinho.isVazio());
    }

    @Test
    void listaDeItensNaoPodeSerAlteradaPorFora() {
        carrinho.adicionar(obra(1, "A", "Óleo", "100"));
        assertThrows(UnsupportedOperationException.class, () -> carrinho.getItens().clear());
    }
}
