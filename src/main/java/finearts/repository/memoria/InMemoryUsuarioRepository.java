package finearts.repository.memoria;

import finearts.domain.Artista;
import finearts.domain.Cliente;
import finearts.domain.Usuario;
import finearts.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryUsuarioRepository implements UsuarioRepository {
    private final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    private int sequencia = 0;

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.values().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    @Override
    public boolean existePorEmail(String email) { return buscarPorEmail(email).isPresent(); }

    @Override
    public boolean existePorCpf(String cpf) {
        return usuarios.values().stream().anyMatch(u -> u.getCpf().equals(cpf));
    }

    @Override
    public Artista salvar(Artista artista) { armazenar(artista); return artista; }

    @Override
    public Cliente salvar(Cliente cliente) { armazenar(cliente); return cliente; }

    @Override
    public boolean aprovarArtista(int artistaId) {
        Usuario u = usuarios.get(artistaId);
        if (u instanceof Artista) {
            ((Artista) u).aprovar();
            return true;
        }
        return false;
    }

    @Override
    public List<Artista> listarArtistasPendentes() {
        List<Artista> pendentes = new ArrayList<>();
        for (Usuario u : usuarios.values()) {
            if (u instanceof Artista && !((Artista) u).isAprovado()) {
                pendentes.add((Artista) u);
            }
        }
        return pendentes;
    }

    private void armazenar(Usuario u) {
        u.definirId(++sequencia);
        usuarios.put(u.getId(), u);
    }
}
