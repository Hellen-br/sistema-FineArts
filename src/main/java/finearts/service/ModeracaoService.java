package finearts.service;

import finearts.domain.Administrador;
import finearts.domain.Artista;
import finearts.domain.excecao.ValidacaoException;
import finearts.repository.UsuarioRepository;
import java.util.List;

/** RF013: moderação feita pelo administrador. */
public class ModeracaoService {
    private final UsuarioRepository usuarios;

    public ModeracaoService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public List<Artista> listarArtistasPendentes(Administrador admin) {
        return usuarios.listarArtistasPendentes();
    }

    public void aprovarArtista(Administrador admin, int artistaId) {
        if (!usuarios.aprovarArtista(artistaId)) {
            throw new ValidacaoException("Artista não encontrado.");
        }
    }
}
