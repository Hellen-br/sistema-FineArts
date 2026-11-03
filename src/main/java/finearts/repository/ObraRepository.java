package finearts.repository;

import finearts.domain.FiltroObras;
import finearts.domain.ObraDeArte;
import java.util.List;
import java.util.Optional;

public interface ObraRepository {
    ObraDeArte salvar(ObraDeArte obra);
    Optional<ObraDeArte> buscarPorId(int id);
    List<ObraDeArte> pesquisar(FiltroObras filtro);
    void excluir(int id);
}
