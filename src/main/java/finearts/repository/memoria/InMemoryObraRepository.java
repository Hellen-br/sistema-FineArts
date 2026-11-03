package finearts.repository.memoria;

import finearts.domain.FiltroObras;
import finearts.domain.ObraDeArte;
import finearts.repository.ObraRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryObraRepository implements ObraRepository {
    private final Map<Integer, ObraDeArte> obras = new LinkedHashMap<>();
    private int sequencia = 0;

    @Override
    public ObraDeArte salvar(ObraDeArte obra) {
        obra.definirId(++sequencia);
        obras.put(obra.getId(), obra);
        return obra;
    }

    @Override
    public Optional<ObraDeArte> buscarPorId(int id) { return Optional.ofNullable(obras.get(id)); }

    @Override
    public List<ObraDeArte> pesquisar(FiltroObras filtro) {
        List<ObraDeArte> resultado = new ArrayList<>();
        for (ObraDeArte o : obras.values()) {
            if (filtro.aceita(o)) {
                resultado.add(o);
            }
        }
        resultado.sort(Comparator.comparing(ObraDeArte::getTitulo, String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    @Override
    public void excluir(int id) { obras.remove(id); }
}
