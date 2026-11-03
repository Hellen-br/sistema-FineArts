package finearts.domain;

import java.time.LocalDate;

public class Artista extends Usuario {
    private String telefone;
    private String formacao;
    private String redesSociais;
    private boolean aprovado;

    public Artista(int id, String nome, String cpf, String email, String senhaHash, LocalDate dataCadastro) {
        super(id, nome, cpf, email, senhaHash, dataCadastro);
    }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.ARTISTA; }

    public void aprovar() { this.aprovado = true; }
    public boolean isAprovado() { return aprovado; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getFormacao() { return formacao; }
    public void setFormacao(String formacao) { this.formacao = formacao; }
    public String getRedesSociais() { return redesSociais; }
    public void setRedesSociais(String redesSociais) { this.redesSociais = redesSociais; }
}
