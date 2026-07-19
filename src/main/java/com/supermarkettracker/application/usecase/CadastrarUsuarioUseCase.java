package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.CadastrarUsuarioCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.ConflitoDeDominioException;
import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.UsuarioRepository;
import java.time.Instant;
public final class CadastrarUsuarioUseCase {
    private final UsuarioRepository usuarios; public CadastrarUsuarioUseCase(UsuarioRepository usuarios) { this.usuarios = usuarios; }
    public Usuario executar(CadastrarUsuarioCommand c) { ValidacaoCommand.obrigatorio(c.authUserId(), "Usuario de autenticacao"); ValidacaoCommand.obrigatorio(c.nome(), "Nome"); Email email = new Email(c.email()); if (usuarios.buscarPorEmail(email).isPresent()) throw new ConflitoDeDominioException("E-mail ja cadastrado"); Instant agora = Instant.now(); return usuarios.salvar(new Usuario(Identificador.novo(), new Identificador(c.authUserId()), c.nome(), email, c.telefone(), StatusAtivo.ATIVO, agora, agora)); }
}
