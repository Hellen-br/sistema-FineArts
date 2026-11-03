package finearts.repository;

import finearts.domain.Artista;
import finearts.domain.Cliente;
import finearts.domain.Usuario;
import java.util.List;
import java.util.Optional;

/** Contrato de persistência de usuários (DIP: os serviços dependem disto, não do JDBC). */
public interface UsuarioRepository {
    Optional<Usuario> buscarPorEmail(String email);
    boolean existePorEmail(String email);
    boolean existePorCpf(String cpf);
    Artista salvar(Artista artista);
    Cliente salvar(Cliente cliente);
    /** @return true se um artista foi aprovado; false se o id não existe. */
    boolean aprovarArtista(int artistaId);
    List<Artista> listarArtistasPendentes();
}
