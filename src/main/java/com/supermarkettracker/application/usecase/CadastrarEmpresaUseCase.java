package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.CadastrarEmpresaCommand;
import com.supermarkettracker.application.dto.EmpresaDto;
import com.supermarkettracker.application.mapper.EmpresaMapper;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.Empresa;
import com.supermarkettracker.domain.model.enums.StatusEmpresa;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.EmpresaRepository;
import java.time.Instant;
public final class CadastrarEmpresaUseCase {
    private final EmpresaRepository empresas; public CadastrarEmpresaUseCase(EmpresaRepository empresas) { this.empresas = empresas; }
    public EmpresaDto executar(CadastrarEmpresaCommand c) { ValidacaoCommand.obrigatorio(c.razaoSocial(), "Razao social"); ValidacaoCommand.obrigatorio(c.nomeFantasia(), "Nome fantasia"); Instant agora = Instant.now(); Empresa e = new Empresa(Identificador.novo(), c.razaoSocial(), c.nomeFantasia(), c.cnpj() == null ? null : new Documento(c.cnpj()), StatusEmpresa.ATIVA, agora, agora); return EmpresaMapper.paraDto(empresas.salvar(e)); }
}
