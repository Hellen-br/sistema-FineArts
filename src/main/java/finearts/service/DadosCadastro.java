package finearts.service;

import finearts.domain.PerfilCliente;

/** Parâmetro-objeto do cadastro (evita métodos com 8+ parâmetros). */
public class DadosCadastro {
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private String confirmacaoSenha;
    private String telefone;
    private String formacao;
    private String redesSociais;
    private PerfilCliente perfilCliente;

    public DadosCadastro nome(String v) { this.nome = v; return this; }
    public DadosCadastro cpf(String v) { this.cpf = v; return this; }
    public DadosCadastro email(String v) { this.email = v; return this; }
    public DadosCadastro senha(String v) { this.senha = v; return this; }
    public DadosCadastro confirmacaoSenha(String v) { this.confirmacaoSenha = v; return this; }
    public DadosCadastro telefone(String v) { this.telefone = v; return this; }
    public DadosCadastro formacao(String v) { this.formacao = v; return this; }
    public DadosCadastro redesSociais(String v) { this.redesSociais = v; return this; }
    public DadosCadastro perfilCliente(PerfilCliente v) { this.perfilCliente = v; return this; }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getConfirmacaoSenha() { return confirmacaoSenha; }
    public String getTelefone() { return telefone; }
    public String getFormacao() { return formacao; }
    public String getRedesSociais() { return redesSociais; }
    public PerfilCliente getPerfilCliente() { return perfilCliente; }
}
