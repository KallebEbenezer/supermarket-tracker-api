package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.CadastrarClienteCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.Cliente;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ClienteRepository;
import java.time.Instant;
public final class CadastrarClienteUseCase { private final ClienteRepository clientes; public CadastrarClienteUseCase(ClienteRepository clientes) { this.clientes = clientes; }
    public Cliente executar(CadastrarClienteCommand c) { ValidacaoCommand.obrigatorio(c.empresaId(), "Empresa"); ValidacaoCommand.obrigatorio(c.nome(), "Nome"); Instant agora = Instant.now(); return clientes.salvar(new Cliente(Identificador.novo(), new Identificador(c.empresaId()), c.nome(), c.cpfCnpj() == null ? null : new Documento(c.cpfCnpj()), c.email() == null ? null : new Email(c.email()), c.telefone(), c.dataNascimento(), StatusAtivo.ATIVO, agora, agora)); } }
