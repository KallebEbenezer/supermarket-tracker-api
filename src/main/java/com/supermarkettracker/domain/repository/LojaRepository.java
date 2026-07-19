package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Loja;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;
import java.util.Optional;

public interface LojaRepository { Loja salvar(Loja loja); Optional<Loja> buscarPorId(Identificador id); List<Loja> listarPorEmpresa(Identificador empresaId); }
