package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusSessaoCaixa;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record SessaoCaixa(Identificador id, Identificador caixaId, Identificador usuarioAberturaId,
                          Identificador usuarioFechamentoId, Instant abertoEm, Instant fechadoEm,
                          Dinheiro valorAbertura, Dinheiro valorFechamentoInformado,
                          StatusSessaoCaixa status, String observacao) { }
