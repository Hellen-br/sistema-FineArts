package finearts.domain;

import java.time.LocalDate;

/** Superclasse de Administrador, Artista e Cliente. Não conhece persistência nem interface. */
public abstract class Usuario {
    private int id;
    private final String nome;
    private final String cpf;
    private final String email;
    private final String senhaHash;
    private final LocalDate dataCadastro;

    protected Usuario(int id, String nome, String cpf, String email, String senhaHash, LocalDate dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senhaHash = senhaHash;
        this.dataCadastro = dataCadastro;
    }

    public abstract TipoUsuario getTipo();

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public LocalDate getDataCadastro() { return dataCadastro; }

    /** Chamado pelo repositório após o INSERT; só pode ocorrer uma vez. */
    public void definirId(int novoId) {
        if (id != 0) {
            throw new IllegalStateException("Identificador já definido.");
        }
        this.id = novoId;
    }
}
