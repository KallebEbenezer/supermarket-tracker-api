package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Caixa;
import com.supermarkettracker.domain.model.SessaoCaixa;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;
import java.util.Optional;

public interface CaixaRepository { Caixa salvar(Caixa caixa); Optional<Caixa> buscarPorId(Identificador id); List<Caixa> listarPorLoja(Identificador lojaId); List<Caixa> listarPorEmpresa(Identificador empresaId); SessaoCaixa salvarSessao(SessaoCaixa sessao); Optional<SessaoCaixa> buscarSessaoAberta(Identificador caixaId); void excluirPorId(Identificador id); }
