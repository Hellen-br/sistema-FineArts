package finearts.service;

import finearts.domain.Usuario;
import finearts.domain.excecao.CredenciaisInvalidasException;
import finearts.repository.UsuarioRepository;
import finearts.seguranca.PasswordEncoder;

public class AutenticacaoService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public AutenticacaoService(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) {
            throw new CredenciaisInvalidasException();
        }
        return usuarios.buscarPorEmail(email.trim().toLowerCase())
                .filter(u -> encoder.confere(senha, u.getSenhaHash()))
                .orElseThrow(CredenciaisInvalidasException::new);
    }
}
