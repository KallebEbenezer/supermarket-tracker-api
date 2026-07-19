package com.supermarkettracker.application.validator;

import com.supermarkettracker.domain.exception.RegraDeDominioException;
import java.math.BigDecimal;

public final class ValidacaoCommand {
    private ValidacaoCommand() { }
    public static void obrigatorio(Object valor, String campo) { if (valor == null || (valor instanceof String texto && texto.isBlank())) throw new RegraDeDominioException(campo + " e obrigatorio"); }
    public static void positivo(BigDecimal valor, String campo) { obrigatorio(valor, campo); if (valor.signum() <= 0) throw new RegraDeDominioException(campo + " deve ser positivo"); }
    public static void naoNegativo(BigDecimal valor, String campo) { obrigatorio(valor, campo); if (valor.signum() < 0) throw new RegraDeDominioException(campo + " nao pode ser negativo"); }
}
