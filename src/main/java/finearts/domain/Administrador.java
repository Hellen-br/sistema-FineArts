package finearts.domain;

import java.time.LocalDate;

public class Administrador extends Usuario {
    public Administrador(int id, String nome, String cpf, String email, String senhaHash, LocalDate dataCadastro) {
        super(id, nome, cpf, email, senhaHash, dataCadastro);
    }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.ADMINISTRADOR; }
}
