package finearts.domain;

import java.time.LocalDate;

public class Cliente extends Usuario {
    private String telefone;
    private PerfilCliente perfil;

    public Cliente(int id, String nome, String cpf, String email, String senhaHash, LocalDate dataCadastro) {
        super(id, nome, cpf, email, senhaHash, dataCadastro);
    }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.CLIENTE; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public PerfilCliente getPerfil() { return perfil; }
    public void setPerfil(PerfilCliente perfil) { this.perfil = perfil; }
}
