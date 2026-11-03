package finearts.service;

import finearts.domain.Artista;
import finearts.domain.Cliente;
import finearts.domain.Cpf;
import finearts.domain.PerfilCliente;
import finearts.domain.excecao.ValidacaoException;
import finearts.repository.UsuarioRepository;
import finearts.seguranca.PasswordEncoder;
import java.time.LocalDate;

public class CadastroService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final ValidadorCadastro validador;

    public CadastroService(UsuarioRepository usuarios, PasswordEncoder encoder, ValidadorCadastro validador) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.validador = validador;
    }

    public Cliente cadastrarCliente(DadosCadastro dados) {
        validarEUnicidade(dados);
        Cliente cliente = new Cliente(0, dados.getNome().trim(), Cpf.formatar(dados.getCpf()),
                emailNormalizado(dados), encoder.codificar(dados.getSenha()), LocalDate.now());
        cliente.setTelefone(dados.getTelefone());
        cliente.setPerfil(dados.getPerfilCliente() != null ? dados.getPerfilCliente() : PerfilCliente.APRECIADOR);
        return usuarios.salvar(cliente);
    }

    /** O artista nasce pendente: só publica obras depois da aprovação do administrador. */
    public Artista cadastrarArtista(DadosCadastro dados) {
        validarEUnicidade(dados);
        Artista artista = new Artista(0, dados.getNome().trim(), Cpf.formatar(dados.getCpf()),
                emailNormalizado(dados), encoder.codificar(dados.getSenha()), LocalDate.now());
        artista.setTelefone(dados.getTelefone());
        artista.setFormacao(dados.getFormacao());
        artista.setRedesSociais(dados.getRedesSociais());
        return usuarios.salvar(artista);
    }

    private void validarEUnicidade(DadosCadastro dados) {
        validador.validar(dados);
        if (usuarios.existePorEmail(emailNormalizado(dados))) {
            throw new ValidacaoException("E-mail já cadastrado.");
        }
        if (usuarios.existePorCpf(Cpf.formatar(dados.getCpf()))) {
            throw new ValidacaoException("CPF já cadastrado.");
        }
    }

    private static String emailNormalizado(DadosCadastro dados) {
        return dados.getEmail().trim().toLowerCase();
    }
}
