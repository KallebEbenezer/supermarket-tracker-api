package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.CadastrarLojaCommand;
import com.supermarkettracker.application.dto.LojaDto;
import com.supermarkettracker.application.mapper.LojaMapper;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.Loja;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.LojaRepository;
import java.time.Instant;
public final class CadastrarLojaUseCase { private final LojaRepository lojas; public CadastrarLojaUseCase(LojaRepository lojas) { this.lojas = lojas; }
    public LojaDto executar(CadastrarLojaCommand c) { ValidacaoCommand.obrigatorio(c.empresaId(), "Empresa"); ValidacaoCommand.obrigatorio(c.nome(), "Nome"); Instant agora = Instant.now(); String codigo = (c.codigo() == null || c.codigo().isBlank()) ? gerarCodigo() : c.codigo(); Loja l = new Loja(Identificador.novo(), new Identificador(c.empresaId()), codigo, c.nome(), c.cnpj() == null ? null : new Documento(c.cnpj()), null, c.telefone(), StatusAtivo.ATIVO, agora, agora); return LojaMapper.paraDto(lojas.salvar(l)); }

    private static String gerarCodigo() { return "LOJA-" + Identificador.novo().valor().toString().substring(0, 8).toUpperCase(); } }
