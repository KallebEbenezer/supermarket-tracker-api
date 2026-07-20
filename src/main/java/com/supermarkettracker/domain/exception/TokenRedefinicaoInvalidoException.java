package com.supermarkettracker.domain.exception;

/** Lançada quando o token de redefinição de senha é inválido ou expirou. */
public class TokenRedefinicaoInvalidoException extends RuntimeException {
    public TokenRedefinicaoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
