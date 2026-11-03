package finearts.service;

import finearts.domain.Artista;
import finearts.domain.ComissaoPlataforma;
import finearts.domain.Dimensoes;
import finearts.domain.FiltroObras;
import finearts.domain.ObraDeArte;
import finearts.domain.excecao.AcessoNegadoException;
import finearts.domain.excecao.NegocioException;
import finearts.domain.excecao.ValidacaoException;
import finearts.repository.ObraRepository;
import java.math.BigDecimal;
import java.util.List;

public class CatalogoService {
    private final ObraRepository obras;
    private final ValidadorObra validador;
    private final ComissaoPlataforma comissao;

    public CatalogoService(ObraRepository obras, ValidadorObra validador) {
        this(obras, validador, new ComissaoPlataforma());
    }

    public CatalogoService(ObraRepository obras, ValidadorObra validador, ComissaoPlataforma comissao) {
        this.obras = obras;
        this.validador = validador;
        this.comissao = comissao;
    }

    public List<ObraDeArte> pesquisar(FiltroObras filtro) {
        return obras.pesquisar(filtro);
    }

    public ObraDeArte publicar(Artista artista, DadosObra dados) {
        if (!artista.isAprovado()) {
            throw new AcessoNegadoException("Artista ainda não aprovado pelo administrador.");
        }
        validador.validar(dados);
        ObraDeArte obra = ObraDeArte.builder()
                .artistaId(artista.getId())
                .nomeArtista(artista.getNome())
                .titulo(dados.getTitulo().trim())
                .tecnica(dados.getTecnica().trim())
                .estilo(valorOuPadrao(dados.getEstilo()))
                .local(valorOuPadrao(dados.getLocal()))
                .ano(dados.getAno())
                .dimensoes(new Dimensoes(dados.getLarguraCm(), dados.getAlturaCm()))
                .preco(dados.getPreco())
                .build();
        return obras.salvar(obra);
    }

    public void remover(Artista artista, int obraId) {
        ObraDeArte obra = obras.buscarPorId(obraId)
                .orElseThrow(() -> new ValidacaoException("Obra não encontrada."));
        if (obra.getArtistaId() != artista.getId()) {
            throw new AcessoNegadoException("Você só pode remover as suas próprias obras.");
        }
        if (!obra.isDisponivel()) {
            throw new NegocioException("Obra já vendida não pode ser removida.");
        }
        obras.excluir(obraId);
    }

    public ResumoArtista resumoDoArtista(int artistaId) {
        List<ObraDeArte> todas = obras.pesquisar(new FiltroObras().artista(artistaId).incluindoIndisponiveis());
        int vendidas = 0;
        BigDecimal valorVendido = BigDecimal.ZERO;
        BigDecimal repasse = BigDecimal.ZERO;
        for (ObraDeArte o : todas) {
            if (!o.isDisponivel()) {
                vendidas++;
                valorVendido = valorVendido.add(o.getPreco());
                repasse = repasse.add(comissao.repasseAoArtista(o.getPreco()));
            }
        }
        return new ResumoArtista(todas.size(), vendidas, valorVendido, repasse);
    }

    private static String valorOuPadrao(String valor) {
        return valor == null || valor.trim().isEmpty() ? "Não informado" : valor.trim();
    }
}
