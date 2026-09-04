package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.CadastrarEmpresaCommand;
import com.supermarkettracker.application.dto.EmpresaDto;
import com.supermarkettracker.application.mapper.EmpresaMapper;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.Empresa;
import com.supermarkettracker.domain.model.EmpresaUsuario;
import com.supermarkettracker.domain.model.enums.PapelUsuario;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.enums.StatusEmpresa;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.EmpresaRepository;
import com.supermarkettracker.domain.repository.EmpresaUsuarioRepository;
import java.time.Instant;
public final class CadastrarEmpresaUseCase {
    private final EmpresaRepository empresas; private final EmpresaUsuarioRepository empresaUsuarios;
    public CadastrarEmpresaUseCase(EmpresaRepository empresas, EmpresaUsuarioRepository empresaUsuarios) { this.empresas = empresas; this.empresaUsuarios = empresaUsuarios; }
    public EmpresaDto executar(CadastrarEmpresaCommand c) { ValidacaoCommand.obrigatorio(c.razaoSocial(), "Razao social"); ValidacaoCommand.obrigatorio(c.nomeFantasia(), "Nome fantasia"); Instant agora = Instant.now(); Empresa e = new Empresa(Identificador.novo(), c.razaoSocial(), c.nomeFantasia(), c.cnpj() == null ? null : new Documento(c.cnpj()), StatusEmpresa.ATIVA, agora, agora); Empresa salva = empresas.salvar(e); if (c.usuarioId() != null) { Identificador uid = new Identificador(c.usuarioId()); empresaUsuarios.salvar(new EmpresaUsuario(Identificador.novo(), salva.id(), uid, PapelUsuario.PROPRIETARIO, StatusAtivo.ATIVO, agora, agora)); } return EmpresaMapper.paraDto(salva); }
}
