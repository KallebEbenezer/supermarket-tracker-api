package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;
import java.util.Optional;

public interface PagamentoRepository { Pagamento salvar(Pagamento pagamento); Optional<Pagamento> buscarPorId(Identificador id); List<Pagamento> listarPorVenda(Identificador vendaId); }
